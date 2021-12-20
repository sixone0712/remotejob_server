package jp.co.canon.rss.logmanager.scheduler;

import ch.qos.logback.classic.Logger;
import com.google.gson.Gson;
import jp.co.canon.rss.logmanager.config.ReqURLController;
import jp.co.canon.rss.logmanager.dto.job.ResStepRunDTO;
import jp.co.canon.rss.logmanager.dto.job.ResStepStatusDTO;
import jp.co.canon.rss.logmanager.exception.ConvertException;
import jp.co.canon.rss.logmanager.repository.*;
import jp.co.canon.rss.logmanager.util.*;
import jp.co.canon.rss.logmanager.vo.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Slf4j
public abstract class DynamicAbstractScheduler {
    @Value("${logmonitor.logging.root}")
    private String loggingPath;

    @Value("${file.download-dir}")
    private String downloadPath;

    private ThreadPoolTaskScheduler scheduler;
    private Map<String, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

    @Autowired
    private HistoryRepository historyRepository;

    @Autowired
    private SchedulerRepository schedulerRepository;

    @Autowired
    private RemoteJobRepository remoteJobRepository;

    @Autowired
    private LocalJobRepository localJobRepository;

    @Autowired
    SiteRepository siteRepository;

    @PostConstruct
    private void _init() throws InterruptedException {
        schedulerRepository.deleteAll();

        List<RemoteJobVo> remoteJobVoList = (List<RemoteJobVo>) remoteJobRepository.findAll();
        for(RemoteJobVo remoteJobVo : remoteJobVoList) {
            stopScheduler(remoteJobVo.getJobId(), ReqURLController.JOB_THREAD_JOB);
            stopScheduler(remoteJobVo.getJobId(), ReqURLController.JOB_THREAD_NOTI);
            remoteJobRepository.updateStopStatus(remoteJobVo.getJobId(), true);
        }

        List<LocalJobVo> localJobVoList = localJobRepository.findAll();
        for(LocalJobVo localJobVo : localJobVoList) {
            stopScheduler(localJobVo.getJobId(), ReqURLController.JOB_THREAD_JOB);
            localJobRepository.updateStopStatus(localJobVo.getJobId(), true);
        }
    }

    public void stopScheduler(int jobId, String flag) throws InterruptedException {
        List<HistoryVo> historyVoList = historyRepository.findByJobId(jobId, Sort.by(Sort.Direction.DESC, "id"));

        for (HistoryVo historyVo : historyVoList) {
            if(historyVo.getEndingTime() == null
                    || historyVo.getStatus().equals(ReqURLController.JOB_STATUS_NOTBUILD)
                    || historyVo.getStatus().equals(ReqURLController.JOB_STATUS_PROCESSING)
                    || historyVo.getStatus().equals(ReqURLController.JOB_STATUS_TIMEOUT)) {
                historyRepository.updateHistoryStatus(historyVo.getId(), ReqURLController.JOB_STATUS_FAILURE);
                historyRepository.updateHistoryEndingTime(historyVo.getId(),
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

                String errorMsg = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                        + " : This job was forced to end.";
                switch (historyVo.getStep()) {
                    case (ReqURLController.JOB_STEP_COLLECT):
                        remoteJobRepository.updateCollectStatus(historyVo.getJobId(), ReqURLController.JOB_STATUS_FAILURE);
                        remoteJobRepository.updateCollectError(historyVo.getJobId(), errorMsg);
                        break;
                    case (ReqURLController.JOB_STEP_CONVERT):
                        remoteJobRepository.updateConvertStatus(historyVo.getJobId(), ReqURLController.JOB_STATUS_FAILURE);
                        remoteJobRepository.updateConvertError(historyVo.getJobId(), errorMsg);
                        break;
                    case (ReqURLController.JOB_STEP_ERROR):
                        remoteJobRepository.updateErrorSummaryStatus(historyVo.getJobId(), ReqURLController.JOB_STATUS_FAILURE);
                        remoteJobRepository.updateErrorSummaryError(historyVo.getJobId(), errorMsg);
                        break;
                    case (ReqURLController.JOB_STEP_CRAS):
                        remoteJobRepository.updateCrasStatus(historyVo.getJobId(), ReqURLController.JOB_STATUS_FAILURE);
                        remoteJobRepository.updateCrasError(historyVo.getJobId(), errorMsg);
                        break;
                    case (ReqURLController.JOB_STEP_VERSION):
                        remoteJobRepository.updateVersionCheckStatus(historyVo.getJobId(), ReqURLController.JOB_STATUS_FAILURE);
                        remoteJobRepository.updateVersionCheckError(historyVo.getJobId(), errorMsg);
                        break;
                    case (ReqURLController.JOB_STEP_DBPURGE):
                        remoteJobRepository.updateDbPurgeStatus(historyVo.getJobId(), ReqURLController.JOB_STATUS_FAILURE);
                        remoteJobRepository.updateDbPurgeError(historyVo.getJobId(), errorMsg);
                        break;
                }
            }
        }
        log.info("All tasks are forced to end.");

        List<String> taskNameList = new ArrayList<>();
        switch (flag) {
            case(ReqURLController.JOB_THREAD_JOB) :
                taskNameList.add(ReqURLController.JOB_TYPE_REMOTEJOB + "-" + ReqURLController.JOB_STEP_COLLECT + "-" + jobId);
                taskNameList.add(ReqURLController.JOB_TYPE_REMOTEJOB + "-" + ReqURLController.JOB_STEP_CONVERT + "-" + jobId);
                log.info("create job task list for stop");
                break;
            case(ReqURLController.JOB_THREAD_NOTI) :
                taskNameList.add(ReqURLController.JOB_TYPE_REMOTEJOB + "-" + ReqURLController.JOB_STEP_ERROR + "-" + jobId);
                taskNameList.add(ReqURLController.JOB_TYPE_REMOTEJOB + "-" + ReqURLController.JOB_STEP_CRAS + "-" + jobId);
                taskNameList.add(ReqURLController.JOB_TYPE_REMOTEJOB + "-" + ReqURLController.JOB_STEP_VERSION + "-" + jobId);
                taskNameList.add(ReqURLController.JOB_TYPE_REMOTEJOB + "-" + ReqURLController.JOB_STEP_DBPURGE + "-" + jobId);
                log.info("create noti task list for stop");
                break;
        }
        if(scheduledTasks != null && taskNameList != null) {
            for (String taskName : taskNameList) {
                for (int i = 0; i < scheduledTasks.size(); i++) {
                    if(scheduledTasks.get(taskName) != null) {
                        scheduledTasks.get(taskName).cancel(true);
                        scheduledTasks.remove(taskName);
                        log.info("task canceled : " + taskName);
                    }
                    break;
                }
            }
        }

    }

    public void startScheduler(List<SchedulerVo> schedulerVoList) {
        // Create a scheduler
        scheduler = new ThreadPoolTaskScheduler();
        scheduler.initialize();
        for (SchedulerVo schedulerVo : schedulerVoList) {
            ScheduledFuture<?> task = scheduler.schedule(getRunnable(schedulerVo), getTrigger(schedulerVo.getCron()));
            log.info("create scheduler : " + schedulerVo.getStep() + " / " + schedulerVo.getCron());
            scheduledTasks.put(ReqURLController.JOB_TYPE_REMOTEJOB + "-"
                    + schedulerVo.getStep() + "-" + schedulerVo.getJobId(), task);
        }
    }

    private Runnable getRunnable(SchedulerVo schedulerVo) {
        return new Runnable() {
            @SneakyThrows
            @Override
            public void run()  {
                // 로그파일 생성
                int leftLimit = 48; // numeral '0'
                int rightLimit = 122; // letter 'z'
                int targetStringLength = 10;
                Random random = new Random();
                String historyName = null;
                switch (schedulerVo.getType()) {
                    case (ReqURLController.JOB_TYPE_REMOTEJOB):
                    case (ReqURLController.JOB_TYPE_MANUALJOB):
                        historyName = ReqURLController.JOB_TYPE_REMOTEJOB
                                + "_" + schedulerVo.getStep()
                                + "_" + String.format("%06d", schedulerVo.getJobId()) + "_"
                                + random.ints(leftLimit, rightLimit + 1)
                                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                                .limit(targetStringLength)
                                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                                .toString();
                        break;
                    case (ReqURLController.JOB_TYPE_LOCALJOB):
                        historyName = ReqURLController.JOB_TYPE_LOCALJOB
                                + "_" + schedulerVo.getStep()
                                + "_" + String.format("%06d", schedulerVo.getJobId()) + "_"
                                + random.ints(leftLimit, rightLimit + 1)
                                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                                .limit(targetStringLength)
                                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                                .toString();
                        break;
                }
                String filename = historyName + ".log";

                File file = Paths.get(loggingPath + historyName + "/", filename).toFile();
                FileLog fileLog = new FileLog(file, historyName);
                Logger logger = fileLog.getLogger();

                // cras server 주소
                String crasServer = null;
                Optional<SiteVo> siteVo = Optional.of(new SiteVo());
                switch (schedulerVo.getType()) {
                    case (ReqURLController.JOB_TYPE_REMOTEJOB):
                    case (ReqURLController.JOB_TYPE_MANUALJOB):
                        siteVo = siteRepository.findById(schedulerVo.getRemoteJobVo().getSiteId());
                        break;
                    case (ReqURLController.JOB_TYPE_LOCALJOB):
                        siteVo = siteRepository.findById(schedulerVo.getLocalJobVo().getSiteId());
                        break;
                }
                crasServer = String.format(ReqURLController.API_DEFAULT_CRAS_SERVER_JOB,
                        siteVo.get().getCrasAddress(), siteVo.get().getCrasPort());
                logger.info("cras server address : " + crasServer);

                // 메일 송신
                MailSenderSetting mailSenderSetting = new MailSenderSetting();
                JavaMailSender javaMailSender = mailSenderSetting.getMailSenderSetting(siteVo);

                MailSender mailSender = new MailSender();
                GetRecipients getRecipients = new GetRecipients();
                logger.info("Complete Mail Sender Setting");

                List<String> attachFileList = new ArrayList<>();
                String versionHTML = null;
                Boolean mailResult = true;

                try {
                    logger.info("build history ID : " + historyName);

                    String settingTime = null;
                    // cycle or time 설정 체크
                    if (schedulerVo.getCycle().equals("none"))
                        settingTime = String.format("%s %s",
                                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), schedulerVo.getSettingTime());
                    else {
                        Optional<SchedulerVo> schedulerVoNew = schedulerRepository.findById(schedulerVo.getId());
                        settingTime = schedulerVoNew.get().getSettingTime();
                    }
                    logger.info("set setting time : " + settingTime);

                    // history 테이블에 작업 등록
                    HistoryVo historyVo = new HistoryVo();
                    switch (schedulerVo.getType()) {
                        case (ReqURLController.JOB_TYPE_REMOTEJOB):
                        case (ReqURLController.JOB_TYPE_MANUALJOB):
                            historyVo = new HistoryVo()
                                    .setJobId(schedulerVo.getJobId())
                                    .setRequestId(schedulerVo.getType() + "-" + schedulerVo.getStep() + "-" + schedulerVo.getJobId())
                                    .setType(ReqURLController.JOB_TYPE_REMOTEJOB)
                                    .setIsManual(schedulerVo.getType().equals(ReqURLController.JOB_TYPE_MANUALJOB) ? true : false)
                                    .setStep(schedulerVo.getStep())
                                    .setStatus(ReqURLController.JOB_STATUS_PROCESSING)
                                    .setSettingTime(settingTime)
                                    .setRunningTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                                    .setHistoryName(historyName)
                                    .setRemoteJobVo(schedulerVo.getRemoteJobVo());
                            break;
                        case (ReqURLController.JOB_TYPE_LOCALJOB):
                            historyVo = new HistoryVo()
                                    .setJobId(schedulerVo.getJobId())
                                    .setRequestId(schedulerVo.getType() + "-" + ReqURLController.JOB_STEP_CONVERT + "-" + schedulerVo.getJobId())
                                    .setType(ReqURLController.JOB_TYPE_LOCALJOB)
                                    .setIsManual(false)
                                    .setStep(schedulerVo.getStep())
                                    .setStatus(ReqURLController.JOB_STATUS_PROCESSING)
                                    .setSettingTime(settingTime)
                                    .setRunningTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                                    .setHistoryName(historyName)
                                    .setRemoteJobVo(schedulerVo.getRemoteJobVo());
                            break;
                    }

                    HistoryVo historyId = historyRepository.save(historyVo);
                    logger.info("add new step on history table - RequestId : " + historyId.getRequestId());

                    if(historyId == null)
                        new ResponseStatusException(HttpStatus.NOT_FOUND);

                    // 작업 실행
                    logger.info("step runner start");
                    Object response = runner(schedulerVo, logger);
                    logger.info("step runner end");

                    // 리턴 결과 parse(rid 취득)
                    Gson gson = new Gson();
                    ResStepRunDTO resRunCollectDTO = gson.fromJson(response.toString(), ResStepRunDTO.class);
                    logger.info("get rid from cras server - Rid : " + resRunCollectDTO.getRid());

                    // 상태 요청 주소
                    String getStepStatusURL = null;
                    switch (schedulerVo.getStep()) {
                        case (ReqURLController.JOB_STEP_COLLECT):
                            getStepStatusURL = crasServer + ReqURLController.API_GET_POST_COLLECT;
                            break;
                        case (ReqURLController.JOB_STEP_CONVERT):
                        case (ReqURLController.JOB_STEP_CONVERT_LOCAL):
                            getStepStatusURL = crasServer + ReqURLController.API_GET_POST_CONVERT;
                            break;
                        case (ReqURLController.JOB_STEP_ERROR):
                            getStepStatusURL = crasServer + ReqURLController.API_GET_POST_ERROR_SUMMARY;
                            break;
                        case (ReqURLController.JOB_STEP_CRAS):
                            getStepStatusURL = crasServer + ReqURLController.API_GET_POST_CRAS;
                            break;
                        case (ReqURLController.JOB_STEP_VERSION):
                            getStepStatusURL = crasServer + ReqURLController.API_GET_POST_VERSION;
                            break;
                        case (ReqURLController.JOB_STEP_DBPURGE):
                            getStepStatusURL = crasServer + ReqURLController.API_GET_POST_DBPURGE;
                            break;
                    }
                    logger.info("Cras Server request URL : " + getStepStatusURL);

                    CallRestAPI callRestAPI = new CallRestAPI();
                    ResStepStatusDTO resStepStatusDTO = new ResStepStatusDTO();

                    // 상태 확인(to Cras Server)
                    HttpHeaders headers = new HttpHeaders();
                    String clientIdType = schedulerVo.getType().equals(ReqURLController.JOB_TYPE_MANUALJOB) ? ReqURLController.JOB_TYPE_REMOTEJOB : schedulerVo.getType();
                    headers.set(ReqURLController.JOB_CONTENT_TYPE, ReqURLController.JOB_APPLICATION_JSON);
                    headers.set(ReqURLController.JOB_CLIENT_ID, clientIdType + "_" + String.format("%06d", schedulerVo.getJobId()));
                    logger.info("client id : " + clientIdType + "_" + String.format("%06d", schedulerVo.getJobId()));
                    HttpEntity reqHeaders = new HttpEntity<>(headers);
                    String jobStatus = ReqURLController.JOB_STATUS_PROCESSING;

                    long startTime = System.currentTimeMillis();
                    do {
                        if(jobStatus.equals(ReqURLController.JOB_STATUS_TIMEOUT))
                            jobStatus = ReqURLController.JOB_STATUS_FAILURE;
                        else {
                            // 상태 취득
                            ResponseEntity<?> resStepStatus = callRestAPI.getWithCustomHeaderRestAPI(
                                    getStepStatusURL + "/" + resRunCollectDTO.getRid(), reqHeaders, ResStepStatusDTO.class);
                            resStepStatusDTO = (ResStepStatusDTO) resStepStatus.getBody();
                            logger.info("get status of step from cras server : " + resStepStatusDTO.getStatus());

                            switch (resStepStatusDTO.getStatus()) {
                                case (ReqURLController.JOB_STATUS_CRAS_SUCCESS):
                                    jobStatus = ReqURLController.JOB_STATUS_SUCCESS;
                                    break;
                                case (ReqURLController.JOB_STATUS_NODATA):
                                    jobStatus = ReqURLController.JOB_STATUS_NODATA;
                                    break;
                                case (ReqURLController.JOB_STATUS_CRAS_ERROR):
                                case (ReqURLController.JOB_STATUS_CRAS_CANCEL):
                                    jobStatus = ReqURLController.JOB_STATUS_FAILURE;
                                    break;
                                case (ReqURLController.JOB_STATUS_CRAS_IDLE):
                                    jobStatus = ReqURLController.JOB_STATUS_NOTBUILD;
                                    break;
                                case (ReqURLController.JOB_STATUS_CRAS_RUNNING):
                                    jobStatus = ReqURLController.JOB_STATUS_PROCESSING;
                                    break;
                            }
                        }

                        switch (schedulerVo.getStep()) {
                            case (ReqURLController.JOB_STEP_ERROR):
                            case (ReqURLController.JOB_STEP_CRAS):
                            case (ReqURLController.JOB_STEP_VERSION):
                                if (jobStatus.equals(ReqURLController.JOB_STATUS_SUCCESS)) {
                                    try {
                                        for (String downloadUrl : resStepStatusDTO.getDownload_url()) {
                                            String crasServerAdd = String.format("http://%s:%s",
                                                    schedulerVo.getRemoteJobVo().getSiteVoList().getCrasAddress(),
                                                    schedulerVo.getRemoteJobVo().getSiteVoList().getCrasPort());

                                            if (downloadUrl.contains("html")) {
                                                ResponseEntity<?> res = callRestAPI.getWithCustomHeaderRestAPI(
                                                        crasServerAdd + downloadUrl, reqHeaders, String.class);
                                                versionHTML = (String) res.getBody();
                                                logger.info("Cras Server request URL : " + crasServerAdd + downloadUrl);
                                            } else {
                                                logger.info("Cras Server request URL : " + crasServerAdd + downloadUrl);
                                                String[] fileNameSplit = downloadUrl.split("/");
                                                String fileName = LocalDateTime.now().
                                                        format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + "_" + fileNameSplit[fileNameSplit.length - 1];
                                                DownloadStreamInfo res = callRestAPI.getWithCustomHeaderDownloadFileRestAPI(
                                                        crasServerAdd + downloadUrl, reqHeaders, downloadPath, fileName);
                                                attachFileList.add(downloadPath + "/" + fileName);
//                                                File attachFile = new File(downloadPath, fileName);
//                                                OutputStream stream = new BufferedOutputStream(new FileOutputStream(attachFile));
//                                                IOUtils.write((CharSequence) res.getInputStream(), stream);
                                            }
                                        }

                                        if (schedulerVo.getStep().equals(ReqURLController.JOB_STEP_ERROR)
                                                && schedulerVo.getRemoteJobVo().getMailContextVoErrorSummary() != null) {
                                            mailResult = mailSender.sendMessageWithAttachment(javaMailSender,
                                                    schedulerVo.getRemoteJobVo().getSiteVoList().getEmailFrom(),
                                                    getRecipients.getRecipients(schedulerVo.getRemoteJobVo(), ReqURLController.JOB_STEP_ERROR),
                                                    schedulerVo.getRemoteJobVo().getMailContextVoErrorSummary().getSubject(),
                                                    versionHTML == null ? "" : versionHTML,
                                                    attachFileList);
                                        } else if (schedulerVo.getStep().equals(ReqURLController.JOB_STEP_CRAS)
                                                && schedulerVo.getRemoteJobVo().getMailContextVoCrasData() != null) {
                                            mailResult = mailSender.sendMessageWithAttachment(javaMailSender,
                                                    schedulerVo.getRemoteJobVo().getSiteVoList().getEmailFrom(),
                                                    getRecipients.getRecipients(schedulerVo.getRemoteJobVo(), ReqURLController.JOB_STEP_CRAS),
                                                    schedulerVo.getRemoteJobVo().getMailContextVoCrasData().getSubject(),
                                                    versionHTML == null ? "" : versionHTML,
                                                    attachFileList);
                                        } else if (schedulerVo.getStep().equals(ReqURLController.JOB_STEP_VERSION)
                                                && schedulerVo.getRemoteJobVo().getMailContextVoVersion() != null) {
                                            mailResult = mailSender.sendMessageWithAttachment(javaMailSender,
                                                    schedulerVo.getRemoteJobVo().getSiteVoList().getEmailFrom(),
                                                    getRecipients.getRecipients(schedulerVo.getRemoteJobVo(), ReqURLController.JOB_STEP_VERSION),
                                                    schedulerVo.getRemoteJobVo().getMailContextVoVersion().getSubject(),
                                                    versionHTML == null ? "" : versionHTML,
                                                    attachFileList);
                                        }
                                    } catch (Exception e) {
                                        mailResult = false;
                                    }

                                    if (mailResult == true) {
                                        jobStatus = ReqURLController.JOB_STATUS_SUCCESS;
                                        logger.info("Success to send mail.");
                                    } else {
                                        jobStatus = ReqURLController.JOB_STATUS_FAILURE;
                                        String step = schedulerVo.getStep().equals(ReqURLController.JOB_STEP_ERROR) ?
                                                ReqURLController.JOB_STEP_ERROR + " " + ReqURLController.JOB_STEP_SUMMARY_CRAS : schedulerVo.getStep();
                                        String[] errorMsg = {
                                                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                                                        + ":" + step + ": Failed to send mail."
                                        };
                                        resStepStatusDTO.setError(errorMsg);
                                        logger.error("Failed to send mail.");
                                    }
                                }
                                break;
                        }

                        // remotejob 테이블 status, error 컬럼 업데이트
                        String errorDetail = "";

                        if (resStepStatusDTO.getError() != null)
                            if (resStepStatusDTO.getError().length > 0)
                                errorDetail = Arrays.toString(resStepStatusDTO.getError());

                        if (schedulerVo.getType().equals(ReqURLController.JOB_TYPE_LOCALJOB)) {
                            localJobRepository.updateConvertStatus(schedulerVo.getJobId(), jobStatus);
                            localJobRepository.updateConvertError(schedulerVo.getJobId(), errorDetail);
                            logger.info("update status and error column of local job table");
                        } else {
                            switch (schedulerVo.getStep()) {
                                case (ReqURLController.JOB_STEP_COLLECT):
                                    remoteJobRepository.updateCollectStatus(schedulerVo.getJobId(), jobStatus);
                                    remoteJobRepository.updateCollectError(schedulerVo.getJobId(), errorDetail);
                                    break;
                                case (ReqURLController.JOB_STEP_CONVERT):
                                    remoteJobRepository.updateConvertStatus(schedulerVo.getJobId(), jobStatus);
                                    remoteJobRepository.updateConvertError(schedulerVo.getJobId(), errorDetail);
                                    break;
                                case (ReqURLController.JOB_STEP_ERROR):
                                    remoteJobRepository.updateErrorSummaryStatus(schedulerVo.getJobId(), jobStatus);
                                    remoteJobRepository.updateErrorSummaryError(schedulerVo.getJobId(), errorDetail);
                                    break;
                                case (ReqURLController.JOB_STEP_CRAS):
                                    remoteJobRepository.updateCrasStatus(schedulerVo.getJobId(), jobStatus);
                                    remoteJobRepository.updateCrasError(schedulerVo.getJobId(), errorDetail);
                                    break;
                                case (ReqURLController.JOB_STEP_VERSION):
                                    remoteJobRepository.updateVersionCheckStatus(schedulerVo.getJobId(), jobStatus);
                                    remoteJobRepository.updateVersionCheckError(schedulerVo.getJobId(), errorDetail);
                                    break;
                                case (ReqURLController.JOB_STEP_DBPURGE):
                                    remoteJobRepository.updateDbPurgeStatus(schedulerVo.getJobId(), jobStatus);
                                    remoteJobRepository.updateDbPurgeError(schedulerVo.getJobId(), errorDetail);
                                    break;
                            }
                            logger.info("update status and error column of remote job table");
                        }

                        // history 테이블 status, rid 컬럼 업데이트
                        historyRepository.updateHistoryStatus(historyId.getId(), jobStatus);
                        historyRepository.updateRequestIdCras(historyId.getId(), resRunCollectDTO.getRid());
                        logger.info("update status and rid of cras server column of remote job table");

                        // jobStatus가 Error인 경우 메일 송신
                        Boolean isErrorNoticeRemote = false;
                        if (schedulerVo.getRemoteJobVo() != null)
                            isErrorNoticeRemote = schedulerVo.getRemoteJobVo().getIsErrorNotice();

                        Boolean isErrorNoticeLocal = false;
                        if (schedulerVo.getLocalJobVo() != null)
                            isErrorNoticeLocal = schedulerVo.getLocalJobVo().getIsErrorNotice();

                        if (isErrorNoticeRemote || isErrorNoticeLocal) {
                            if (jobStatus.equals(ReqURLController.JOB_STATUS_FAILURE)) {
                                sendErrorNotiMail(schedulerVo, siteVo, mailSender, javaMailSender,
                                        getRecipients,
                                        Arrays.toString(resStepStatusDTO.getError()), logger);
                            }
                        }

                        // job이 종료된 경우 history에 endtime 컬럼 업데이트
                        if (jobStatus.equals(ReqURLController.JOB_STATUS_SUCCESS) || jobStatus.equals(ReqURLController.JOB_STATUS_FAILURE)
                                || jobStatus.equals(ReqURLController.JOB_STATUS_NODATA)) {
                            historyRepository.updateHistoryEndingTime(historyId.getId(),
                                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                            logger.info("update endtime column of history table");
                        }

                        try {
                            Thread.sleep(10 * 1000);
                            if((System.currentTimeMillis()-startTime) > (24 * 60 * 60 * 1000)) {
                                jobStatus = ReqURLController.JOB_STATUS_TIMEOUT;
                                logger.info("Job ended due to timeout occurrence");
                            }
                        } catch (InterruptedException e) {
                            logger.error(e.toString());
                            e.printStackTrace();
                            StringWriter errors = new StringWriter();
                            e.printStackTrace(new PrintWriter(errors));
                            logger.error(errors.toString());
                        }
                    } while (jobStatus.equals(ReqURLController.JOB_STATUS_NOTBUILD)
                            || jobStatus.equals(ReqURLController.JOB_STATUS_PROCESSING)
                            || jobStatus.equals(ReqURLController.JOB_STATUS_TIMEOUT));

                    // cycle인 경우 다음 실행 시간 갱신
                    if (!schedulerVo.getCycle().equals("none")) {
                        DateFormat hourFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Calendar cal = Calendar.getInstance();
                        Date regDate = hourFormat.parse(settingTime);
                        cal.setTime(regDate);

                        if (schedulerVo.getCycle().equals(ReqURLController.JOB_CYCLE_DAY))
                            cal.add(Calendar.DATE, schedulerVo.getPeriod());
                        else if (schedulerVo.getCycle().equals(ReqURLController.JOB_CYCLE_HOUR))
                            cal.add(Calendar.HOUR, schedulerVo.getPeriod());
                        else if (schedulerVo.getCycle().equals(ReqURLController.JOB_CYCLE_MINUTE))
                            cal.add(Calendar.MINUTE, schedulerVo.getPeriod());

                        String nextSettingTime = hourFormat.format(cal.getTime());
                        schedulerRepository.updateSettingTime(schedulerVo.getId(), nextSettingTime);
                        logger.info("if mode is cycle, update setting time : " + nextSettingTime);
                    }
                }
                catch (Exception e) {
                    logger.error(e.toString());
                    e.printStackTrace();
                    StringWriter errors = new StringWriter();
                    e.printStackTrace(new PrintWriter(errors));
                    logger.error(errors.toString());
                }
            }
        };
    }

    public abstract Object runner(SchedulerVo schedulerVo, Logger logger) throws ConvertException, IllegalAccessException, IOException;

    public Trigger getTrigger(String cron) {
        return new CronTrigger(cron);
    }

//    @PreDestroy
//    public void destroy() {
//        stopScheduler();
//    }

    public void manualExcute(RemoteJobVo remoteJobVo, String step) {
        String currentTime = LocalDateTime.now().plusSeconds(ReqURLController.JOB_PLUS_SECOND).format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        String cron = String.format("%s %s %s * * *",
                currentTime.split(":")[2], currentTime.split(":")[1], currentTime.split(":")[0]);

        SchedulerVo schedulerVo = new SchedulerVo()
                .setJobId(remoteJobVo.getJobId())
                .setSettingTime(currentTime)
                .setType(ReqURLController.JOB_TYPE_MANUALJOB)
                .setStep(step)
                .setCron(cron)
                .setCycle("none")
                .setRemoteJobVo(remoteJobVo);

        ScheduledFuture<?> task = scheduler.schedule(getRunnable(schedulerVo), getTrigger(cron));
        scheduledTasks.put(ReqURLController.JOB_TYPE_MANUALJOB + "-"
                + schedulerVo.getStep() + "-" + schedulerVo.getJobId(), task);
        log.info("create new manual scheduler : " + step);
    }

    public void localJobExcute(LocalJobVo getJobInfo, String step) {
        String currentTime = LocalDateTime.now().plusSeconds(ReqURLController.JOB_PLUS_SECOND).format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        String cron = String.format("%s %s %s * * *",
                currentTime.split(":")[2], currentTime.split(":")[1], currentTime.split(":")[0]);

        SchedulerVo schedulerVo = new SchedulerVo()
                .setJobId(getJobInfo.getJobId())
                .setSettingTime(currentTime)
                .setType(ReqURLController.JOB_TYPE_LOCALJOB)
                .setStep(step)
                .setCron(cron)
                .setCycle("none")
                .setLocalJobVo(getJobInfo);

        List<SchedulerVo> localJob = new ArrayList<>();
        localJob.add(schedulerVo);
        startScheduler(localJob);

        log.info("create new manual scheduler : " + step);
    }

    public void sendErrorNotiMail(SchedulerVo schedulerVo, Optional<SiteVo> siteVo,
                                  MailSender mailSender, JavaMailSender javaMailSender, GetRecipients getRecipients,
                                  String body, Logger logger) throws MessagingException {
        Optional<RemoteJobVo> remoteJobVo = remoteJobRepository.findByJobId(schedulerVo.getJobId());

        if(body.contains("NullPointerException") || body == null || body.contains("[]")) {
            String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            body = date + " null data is null";
        }

        body = body.replace("[", "");
        body = body.replace("]", "");
        String bodySplit [] = body.split(" ", 4);

        List<String> status = new ArrayList<>();

        switch (schedulerVo.getStep()) {
            case(ReqURLController.JOB_STEP_COLLECT) :
                status.add("Failure");
                status.add("-");
                status.add("-");
                status.add("-");
                status.add("-");
                status.add("-");
                break;
            case(ReqURLController.JOB_STEP_CONVERT) :
                status.add("-");
                status.add("Failure");
                status.add("-");
                status.add("-");
                status.add("-");
                status.add("-");
                break;
            case(ReqURLController.JOB_STEP_ERROR) :
                status.add("-");
                status.add("-");
                status.add("Failure");
                status.add("-");
                status.add("-");
                status.add("-");
                break;
            case(ReqURLController.JOB_STEP_CRAS) :
                status.add("-");
                status.add("-");
                status.add("-");
                status.add("Failure");
                status.add("-");
                status.add("-");
                break;
            case(ReqURLController.JOB_STEP_VERSION) :
                status.add("-");
                status.add("-");
                status.add("-");
                status.add("-");
                status.add("Failure");
                status.add("-");
                break;
            case(ReqURLController.JOB_STEP_DBPURGE) :
                status.add("-");
                status.add("-");
                status.add("-");
                status.add("-");
                status.add("-");
                status.add("Failure");
                break;
        }

        String emailTemplate =
                "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n" +
                "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
                "<head>\n" +
                "  <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />\n" +
                "  <title>Email Template</title>\n" +
                "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />\n" +
                "  <style type=\"text/css\">\n" +
                "      body {\n" +
                "          font-size: 16px;\n" +
                "      }\n" +
                "  </style>\n" +
                "</head>\n" +
                "<body style=\"margin: 0; padding: 0;\">\n" +
                "<table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" +
                "  <tr>\n" +
                "    <td>\n" +
                "      <table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" style=\"border-collapse: collapse;\">\n" +
                "        <tr>\n" +
                "          <td align=\"center\" valign=\"middle\" bgcolor=\"#001529\" style=\"padding: 40px 0 40px 0;\">\n" +
                "            <div style=\"padding-left: 5px; font-size: 35px; color: white\"><b>Log Monitor Error Notification</b></div>\n" +
                "          </td>\n" +
                "        </tr>\n" +
                "        <tr>\n" +
                "          <td bgcolor=\"#ffffff\" style=\"padding: 10px 10px 30px 10px;\">\n" +
                "            <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" +
                "              <tr>\n" +
                "                <td align=\"center\">\n" +
                "                </td>\n" +
                "              </tr>\n" +
                "              <tr>\n" +
                "                <td align=\"center\" style=\"padding: 40px 0 50px 0; font-size: 35px; color: #f3363b;\"><b>\"An error occured in "
                        + siteVo.get().getCrasCompanyName() + "-" + siteVo.get().getCrasFabName() +"</b>\n" +
                "                  <span style=\"text-decoration: underline; font-size: 35px;\">\n" +
                "                    <b> </b></span><b>!\"</b>\n" +
                "                </td>\n" +
                "              </tr>\n" +
                "              <tr>\n" +
                "                <td height=\"26px\" style=\"padding: 5px 0 5px 5px; font-size: 20px; border: solid; border-color: #70bbd9\">\n" +
                "                  <b>\n" +
                        siteVo.get().getCrasCompanyName() + "-" + siteVo.get().getCrasFabName() +
                "                  </b>\n" +
                "                </td>\n" +
                "              </tr>\n" +
                "              <tr>\n" +
                "                <td height=\"10\" style=\"font-size: 0; line-height: 0;\">&nbsp;</td>\n" +
                "              </tr>\n" +
                "              <tr>\n" +
                "                <td height=\"30px\" bgcolor=\"#70bbd9\" style=\"padding: 5px 0 5px 10px;\">\n" +
                "                  <span style=\"font-size: 18px;\">\n" +
                "                    <b>\n" +
                        remoteJobVo.get().getJobName() +
                "                    </b>\n" +
                "                  </span>\n" +
                "                </td>\n" +
                "              </tr>\n" +
                "              <tr>\n" +
                "                <td style=\"padding: 15px 0 10px 7px;\">\n" +
                "                  <span><b># Abnormal Detection</b></span>\n" +
                "                </td>\n" +
                "              </tr>\n" +
                "              <tr>\n" +
                "                <td>\n" +
                "                  <table border=\"1\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" +
                "                    <tr bgcolor=\"#f0f0f0\" style=\"font-size: 13px;\">\n" +
                "                      <td width=\"70\" align=\"center\" valign=\"middle\">\n" +
                "                        <b>Job Name</b>\n" +
                "                      </td>\n" +
                "                      <td width=\"70\" align=\"center\" valign=\"middle\">\n" +
                "                        <b>User-Fab Name</b>\n" +
                "                      </td>\n" +
                "                      <td width=\"70\" align=\"center\" valign=\"middle\">\n" +
                "                        <b>Collect</b>\n" +
                "                      </td>\n" +
                "                      <td width=\"70\" align=\"center\" valign=\"middle\">\n" +
                "                        <b>Convert & Insert</b>\n" +
                "                      </td>\n" +
                "                      <td width=\"70\" align=\"center\" valign=\"middle\">\n" +
                "                        <b>Error Summary</b>\n" +
                "                      </td>\n" +
                "                      <td width=\"70\" align=\"center\" valign=\"middle\">\n" +
                "                        <b>Cras Data</b>\n" +
                "                      </td>\n" +
                "                      <td width=\"70\" align=\"center\" valign=\"middle\">\n" +
                "                        <b>Version Check</b>\n" +
                "                      </td>\n" +
                "                      <td width=\"70\" align=\"center\" valign=\"middle\">\n" +
                "                        <b>DB Purge</b>\n" +
                "                      </td>\n" +
                "                    </tr>\n" +
                "                   <tr>\n" +
                "                      <td width=\"70\" align=\"center\" valign=\"middle\">\n" +
                        remoteJobVo.get().getJobName() +
                "                      </td>\n" +
                "                      <td width=\"70\" align=\"center\" valign=\"middle\">\n" +
                        siteVo.get().getCrasCompanyName() + "-" + siteVo.get().getCrasFabName() +
                "                      </td>\n" +
                "                      <td width=\"70\" align=\"center\" valign=\"middle\">\n" +
                        status.get(0) +
                "                      </td>\n" +
                "                      <td width=\"70\" align=\"center\" valign=\"middle\">\n" +
                        status.get(1) +
                "                      </td>\n" +
                "                      <td width=\"70\" align=\"center\" valign=\"middle\">\n" +
                        status.get(2) +
                "                      </td>\n" +
                "                      <td width=\"70\" align=\"center\" valign=\"middle\">\n" +
                        status.get(3) +
                "                      </td>\n" +
                "                      <td width=\"70\" align=\"center\" valign=\"middle\">\n" +
                        status.get(4) +
                "                      </td>\n" +
                "                      <td width=\"70\" align=\"center\" valign=\"middle\">\n" +
                        status.get(5) +
                "                      </td>\n" +
                "                    </tr>\n" +
                "                  </table>\n" +
                "                </td>\n" +
                "              </tr>\n" +
                "              <tr>\n" +
                "                <td>\n" +
                "                  <ul>\n" +
                "                    <li>\n" +
                "                      <span style=\"display: inline-block; width: 165px;\"><b>Date</b></span>\n" +
                "                      <span>\n" +
                        bodySplit[0] + " " + bodySplit[1].substring(0, 8) +
                "                      </span>\n" +
                "                    </li>\n" +
                "                    <li>\n" +
                "                      <span style=\"display: inline-block; width: 165px;\"><b>Job Name</b></span>\n" +
                "                      <span>\n" +
                        remoteJobVo.get().getJobName() +
                "                      </span>\n" +
                "                    </li>\n" +
                "                    <li>\n" +
                "                      <span style=\"display: inline-block; width: 165px;\"><b>User-Fab Name</b></span>\n" +
                "                      <span>\n" +
                        siteVo.get().getCrasCompanyName() + "-" + siteVo.get().getCrasFabName() +
                "                      </span>\n" +
                "                    </li>\n" +
                "                    <li>\n" +
                "                      <span style=\"display: inline-block; width: 165px;\"><b>Step</b></span>\n" +
                "                      <span style=\"color: #f3363b\">\n" +
                        schedulerVo.getStep() +
                "                      </span>\n" +
                "                    </li>\n" +
                "                    <li>\n" +
                "                      <span style=\"display: inline-block; width: 165px;\"><b>Reason</b></span>\n" +
                "                      <span style=\"color: #f3363b\">\n" +
                        bodySplit[3] +
                "                      </span>\n" +
                "                    </li>\n" +
                "                  </ul>\n" +
                "                </td>\n" +
                "            </table>\n" +
                "          </td>\n" +
                "        </tr>\n" +
                "      </table>\n" +
                "    </td>\n" +
                "  </tr>\n" +
                "</table>\n" +
                "</body>\n" +
                "</html>";

        if (schedulerVo.getType().equals(ReqURLController.JOB_TYPE_REMOTEJOB)
                || schedulerVo.getType().equals(ReqURLController.JOB_TYPE_MANUALJOB)) {
            mailSender.sendMessageWithAttachment(javaMailSender,
                    schedulerVo.getRemoteJobVo().getSiteVoList().getEmailFrom(),
                    getRecipients.getRecipients(schedulerVo.getRemoteJobVo(), ReqURLController.JOB_STEP_ERR_NOTICE),
                    "[Error Notice] "
                            + schedulerVo.getRemoteJobVo().getSiteVoList().getCrasCompanyName()
                            + "-" + schedulerVo.getRemoteJobVo().getSiteVoList().getCrasFabName(),
                    emailTemplate, new ArrayList<>());
        }
        else if (schedulerVo.getType().equals(ReqURLController.JOB_TYPE_LOCALJOB)) {
            mailSender.sendMessageWithAttachment(javaMailSender,
                    siteVo.get().getEmailFrom(),
                    getRecipients.getRecipientsLocalJob(schedulerVo.getLocalJobVo(), ReqURLController.JOB_STEP_ERR_NOTICE),
                    "[Error Notice] "
                            + siteVo.get().getCrasCompanyName()
                            + "-" + siteVo.get().getCrasFabName(),
                    emailTemplate, new ArrayList<>());
        }
        logger.info("Error Notice Mail Send.");
    }

}


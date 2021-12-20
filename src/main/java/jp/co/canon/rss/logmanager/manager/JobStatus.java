package jp.co.canon.rss.logmanager.manager;

import com.google.gson.Gson;
import jp.co.canon.rss.logmanager.exception.StatusResourceNotFoundException;
import jp.co.canon.rss.logmanager.repository.LocalJobFileIdVoRepository;
import jp.co.canon.rss.logmanager.repository.LocalJobRepository;
import jp.co.canon.rss.logmanager.repository.RemoteJobRepository;
import jp.co.canon.rss.logmanager.system.ClientManageService;
import jp.co.canon.rss.logmanager.vo.LocalJobFileIdVo;
import jp.co.canon.rss.logmanager.vo.LocalJobVo;
import jp.co.canon.rss.logmanager.vo.RemoteJobVo;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.impl.client.BasicResponseHandler;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Getter
public class JobStatus extends Thread{
    protected final String  jobId;
    protected String jobType;
    protected boolean       dead;
    protected AtomicLong works;
    protected AtomicLong finishes;
    protected ClientManageService client;

    private String runId;
    private String prefix;
    private String hostIP;
    private int hostPost;
    private String hostUser;
    private String hostPassword;
    private int[] plan;
    private String[] files;
    private Long fid[];
    private String company;
    private String fab;
    private String uploadPath;
    private String requstId = null;
    private String created;
    private int logCollectBefore;
    /*private StatusDetail statusDetail;*/
    private RemoteJobRepository remoteJobRepository;
    private LocalJobRepository localJobRepository;
    private LocalJobFileIdVoRepository localJobFileIdVoRepository;
    private boolean oldStop = false;

    public JobStatus(
            String jobId,
            String jobType,
            JobManager manager
    ) {

        this.jobId = jobId;
        this.jobType = jobType;
        this.dead = false;
        this.remoteJobRepository = manager.getRemoteJobRepository();
        this.localJobRepository = manager.getLocalJobRepository();
        this.localJobFileIdVoRepository = manager.getLocalJobFileIdVoRepository();
        this.uploadPath = manager.getUploadPath();
        this.logCollectBefore = manager.getLogCollectBefore();

        works = new AtomicLong(0);
        finishes = new AtomicLong(0);
        client = new ClientManageService();
    }

    @Override
    public void run() {
        initialize();

        watch();

        shutdown();
    }

    private void initialize() {
        String[] jobinfo = this.jobId.split("_");
        this.runId = jobinfo[1];

        log.info("initialize START");

        if((this.jobType.equals("remote")) && (this.runId != null)) {
            RemoteJobVo resultRemoteJobDetail = remoteJobRepository.findByJobId(Integer.parseInt(this.jobId))
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

            this.prefix = String.format("http://%s:%d", resultRemoteJobDetail.getSiteVoList().getCrasAddress(), resultRemoteJobDetail.getSiteVoList().getCrasPort());
            this.hostIP = resultRemoteJobDetail.getSiteVoList().getRssAddress();
            this.hostPost = resultRemoteJobDetail.getSiteVoList().getRssPort();
            this.hostUser = resultRemoteJobDetail.getSiteVoList().getRssUserName();
            this.hostPassword = resultRemoteJobDetail.getSiteVoList().getRssPassword();
            this.plan = resultRemoteJobDetail.getPlanIds();
            this.company = resultRemoteJobDetail.getSiteVoList().getCrasCompanyName();
            this.fab = resultRemoteJobDetail.getSiteVoList().getCrasFabName();
            this.created = resultRemoteJobDetail.getCreated().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } else if((this.jobType.equals("local")) && (this.runId != null)) {
//            LocalJobVo resultLocalJobDetail = localJobRepository.findByJobUuid(this.runId).get();
//
//            this.prefix = String.format("http://%s:%d", resultLocalJobDetail.getSiteVoListLocal().getCrasAddress(), resultLocalJobDetail.getSiteVoListLocal().getCrasPort() );
//            int [] fileIndicesSplit = resultLocalJobDetail.getFileIndices();
//            this.files = new String[fileIndicesSplit.length];
//            for (int index=0; index<fileIndicesSplit.length; index++) {
//                Optional<LocalJobFileIdVo> localJobInfo = localJobFileIdVoRepository.findById(fileIndicesSplit[index]);
//                this.files[index] = localJobInfo.get().getFileName();
//            }
//            this.hostIP = resultLocalJobDetail.getSiteVoListLocal().getRssAddress();
//            this.hostPost = resultLocalJobDetail.getSiteVoListLocal().getRssPort();
//            this.hostUser = resultLocalJobDetail.getSiteVoListLocal().getRssUserName();
//            this.hostPassword = resultLocalJobDetail.getSiteVoListLocal().getRssPassword();
//            this.company = resultLocalJobDetail.getSiteVoListLocal().getCrasCompanyName();
//            this.fab = resultLocalJobDetail.getSiteVoListLocal().getCrasFabName();
        } else {
            //none
        }

        log.info("initialize END");

    }

    private boolean isStop() throws StatusTerminateException{
        boolean ret = false;
        try {
            if (this.runId == null) {
                log.info("runid is zero");
                return ret;
            }

            if (this.jobType.equals("remote")) {
                RemoteJobVo resultRemoteJobDetail = remoteJobRepository.findByJobId(Integer.parseInt(this.jobId))
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
                ret = (boolean) resultRemoteJobDetail.isStop();
                if(ret != oldStop) {
                    log.info("isStop:"+ret+" oldStop:"+oldStop);
                    oldStop = ret;
                    initialize();
                }
            }
        } catch (RuntimeException e) {
            throw new StatusTerminateException("job thread is terminated");
        }
        return ret;
    }

    private String getAttiributeFromJson(String json, String attribute){
        JSONParser parser = new JSONParser();
        JSONObject obj = null;

        try {
            obj = (JSONObject)parser.parse(json);
        } catch (ParseException e) {
            log.error("json parseException");
            e.printStackTrace();
        }

        return (String)obj.get(attribute);
    }

    public Long[] getAttiributeFromJsonArray(String json, String attribute){
        JSONParser parser = new JSONParser();
        JSONObject obj = null;

        try {
            obj = (JSONObject)parser.parse(json);
        } catch (ParseException e) {
            log.error("json parseException");
            e.printStackTrace();
        }

        JSONArray jsonArr = (JSONArray) obj.get(attribute);
        Long[] array = new Long[jsonArr.size()];
        for(int idx=0; idx<jsonArr.size(); idx++) {
            array[idx] = (Long)jsonArr.get(idx);
        }

        return array;
    }

    private StatusDetail convertFile() {
        StatusDetail status = new StatusDetail();
        status.setStatus(status.STATUS_IDLE, status.DETAIL_NONE);

        String url = this.prefix + ReqURL.API_CONVERT_FILE;
        String[] path = new String[this.files.length];

        for(int idx = 0; idx<this.files.length; idx++){
            path[idx] = uploadPath + "/" + this.files[idx];
        }

        HttpResponse res = client.postMultipartToMultiFile(url, path);
        if (res.getStatusLine().getStatusCode() == 200) {
            ResponseHandler<String> handler = new BasicResponseHandler();
            try {
                String body = handler.handleResponse(res);
                if (body != null) {
                    this.fid = getAttiributeFromJsonArray(body, "fid");
                    if(this.fid == null){
                        status.setStatus(status.STATUS_ERROR, "fid is null");
                    }
                } else {
                    status.setStatus(status.STATUS_ERROR, "response body is null");
                }
            } catch (Exception e) {
                status.setStatus(status.STATUS_ERROR, e.toString());
            }
        } else {
            status.setStatus(status.STATUS_ERROR, res.getStatusLine().getReasonPhrase());
        }

        return status;
    }

    private StatusDetail convertRun() {
        String url = this.prefix + ReqURL.API_CONVERT_JOB;
        this.requstId = null;
        StatusDetail status = new StatusDetail();
        status.setStatus(status.STATUS_IDLE, status.DETAIL_NONE);
        String msg = null;
        Gson gson = new Gson();

        if (this.jobType.equals("remote")) {
            ReqRemoteConvertRun req = new ReqRemoteConvertRun();

            //source
            req.setSource("rapid");

            //config
            ReqConvertRunConfig config = new ReqConvertRunConfig();
            config.setHost(this.hostIP);
            config.setPort(this.hostPost);
            config.setUser(this.hostUser);
            config.setPassword(this.hostPassword);
            req.setConfig(config);

            //before
            req.setBefore(this.logCollectBefore);

            //plan
            req.setPlan_id(this.plan);

            //created
            req.setCreated(this.created);

            //company, fab
            req.setCompany(this.company);
            req.setFab(this.fab);

            //job_id
            req.setJob_id(this.jobId);

            msg = gson.toJson(req);

        } else if (this.jobType.equals("local")) {
            status = convertFile();

            if(status.getStatus().equals(status.STATUS_ERROR)) {
                return status;
            }

            ReqLocalConvertRun req = new ReqLocalConvertRun();

            //source
            req.setSource("local");

            //file id
            req.setFile(this.fid);

            //config
            ReqConvertRunConfig config = new ReqConvertRunConfig();
            config.setHost(this.hostIP);
            config.setPort(this.hostPost);
            config.setUser(this.hostUser);
            config.setPassword(this.hostPassword);
            req.setConfig(config);

            //company, fab
            req.setCompany(this.company);
            req.setFab(this.fab);

            //job_id
            req.setJob_id(this.jobId);

            msg = gson.toJson(req);
        }

        HttpResponse res = client.post(url, msg);
        if (res.getStatusLine().getStatusCode() == 200) {
            ResponseHandler<String> handler = new BasicResponseHandler();
            try {
                String body = handler.handleResponse(res);
                if (body != null) {
                    this.requstId = getAttiributeFromJson(body, "rid");
                    if(this.requstId == null) {
                        status.setStatus(status.STATUS_ERROR, "request id is null");
                    }
                } else {
                    status.setStatus(status.STATUS_ERROR, "response body is null");
                }
            } catch (Exception e) {
                status.setStatus(status.STATUS_ERROR, e.toString());
            }
        } else if (res.getStatusLine().getStatusCode() == 204) {
            status.setStatus(status.STATUS_IDLE, res.getStatusLine().getReasonPhrase());
            log.info(res.getStatusLine().getReasonPhrase());
        } else {
            status.setStatus(status.STATUS_ERROR, res.getStatusLine().getReasonPhrase());
        }

        return status;
    }

    private StatusDetail convertStatus() {
        StatusDetail status = new StatusDetail();
        status.setStatus(status.STATUS_IDLE, status.DETAIL_NONE);

        String url = this.prefix + ReqURL.API_CONVERT_JOB + "/" +this.requstId;

        HttpResponse res = client.get(url);
        if (res.getStatusLine().getStatusCode() == 200) {
            ResponseHandler<String> handler = new BasicResponseHandler();
            try {
                String body = handler.handleResponse(res);
                if(body != null){
                    String ret = getAttiributeFromJson(body, "status");
                    if(ret != null) {
                        status.setStatus(ret);
                    } else {
                        status.setStatus(status.STATUS_ERROR, "status is null");
                    }
                } else {
                    status.setStatus(status.STATUS_ERROR, "response body is null");
                }
            } catch (Exception e) {
                status.setStatus(status.STATUS_ERROR, e.toString());
            }
        }
        return status;
    }


    private void statusUpdate(String id, String column, StatusDetail status)
            throws StatusResourceNotFoundException {
        if(id == null){
            log.error("ID is zero");
            return;
        }

        if(status.getStatus().equals(status.STATUS_IDLE)){
            status.setStatus(status.STATUS_RUNNING);
        }

        if(this.jobType.equals("remote")) {
            RemoteJobVo remoteJobInfo = remoteJobRepository.findByJobId(Integer.parseInt(this.jobId))
                    .orElseThrow(() -> new StatusResourceNotFoundException("Job not found for this id :: " + id));

            switch (column) {
                case "collectStatus":
                    if(remoteJobInfo.getCollectStatus().equals(status.getStatus()) == false) {
//                        remoteJobRepository.updateCollectStatus(id, status.getStatus());
//                        remoteJobRepository.updateCollectError(id, status.getDetail());
                    }
                    break;
            }
        } else {
//            LocalJobVo localJobInfo = localJobRepository.findByJobUuid(id)
//                    .orElseThrow(() -> new StatusResourceNotFoundException("Job not found for this id :: " + id));
//
//            switch (column) {
//                case "collectStatus":
//                    if(localJobInfo.getCollectStatus().equals(status.getStatus()) == false) {
//                        localJobRepository.updateCollectStatus(id, status.getStatus());
//                    }
//                    break;
//            }
        }
    }

    private void watch() {
        StatusDetail status = new StatusDetail();
        status.setStatus(status.STATUS_IDLE, status.DETAIL_NONE);

        long start = 0;
        try {
            while (true) {
                if (isStop() != true) {
                    if (this.requstId == null) {
                        sleep(30000);
                        status = convertRun();
                        start = System.currentTimeMillis();
                    } else {
                        sleep(10000);
                        status = convertStatus();
                    }

                    long cur = System.currentTimeMillis();
                    if(cur-start>=6000000) {
                        status.setStatus(status.STATUS_ERROR, "converter is timeout");
                    }

                    statusUpdate(this.runId, "collectStatus", status);
                    if ((status.getStatus().equals(status.STATUS_ERROR)) || (status.getStatus().equals(status.STATUS_SUCCESS)) || (status.getStatus().equals(status.STATUS_CANCEL))) {
                        this.requstId = null;
                        log.info(this.jobType + " job(" + this.jobId + " : " + this.company + "-" + this.fab + ") converter is completed... status(" + status.getStatus() + ")");
                        if (this.jobType.equals("local")) {
                            return;
                        }
                    }
                } else {
                    sleep(1000);
                }
            }
        } catch (StatusTerminateException e) {
            log.info("status terminate exception : "+e);
        } catch (StatusResourceNotFoundException e) {
            log.info("status resource not found exception : "+e);
        } catch (InterruptedException e) {
            log.error("interrupted exception : "+e);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void shutdown() {
        dead = true;
    }

    @Setter
    @Accessors(chain = true, fluent = true)
    public static class Builder {
        private String  jobStatusType;
        private JobManager manager;

        public JobStatus build(String key) {

            String id = createJobId(jobStatusType, key);

            if(jobStatusType.equals(JobManager.REMOTE_TYPE)) {
                return new RemoteJobStatus(id, jobStatusType, manager);
            } else if(jobStatusType.equals(JobManager.LOCAL_TYPE)) {
                return new LocalJobStatus(id, jobStatusType, manager);
            }
            return null;
        }

        public String createJobId(String type, String key) {
            final String format = "%sjob_%s";

            return String.format(format, type, key);
        }
    }
}

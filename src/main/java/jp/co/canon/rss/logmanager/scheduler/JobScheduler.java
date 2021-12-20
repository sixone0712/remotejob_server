package jp.co.canon.rss.logmanager.scheduler;

import ch.qos.logback.classic.Logger;
import jp.co.canon.rss.logmanager.config.ReqURLController;
import jp.co.canon.rss.logmanager.dto.job.ReqJobRunDTO;
import jp.co.canon.rss.logmanager.dto.site.ResSitesDetailDTO;
import jp.co.canon.rss.logmanager.exception.ConvertException;
import jp.co.canon.rss.logmanager.repository.LocalJobFileIdVoRepository;
import jp.co.canon.rss.logmanager.repository.SiteRepository;
import jp.co.canon.rss.logmanager.util.CallRestAPI;
import jp.co.canon.rss.logmanager.vo.LocalJobFileIdVo;
import jp.co.canon.rss.logmanager.vo.SchedulerVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class JobScheduler extends DynamicAbstractScheduler {
    @Value("${file.upload-dir}")
    private String uploadPath;

    @Autowired
    LocalJobFileIdVoRepository localJobFileIdVoRepository;

    @Autowired
    SiteRepository siteRepository;

    @Override
    public Object runner(SchedulerVo schedulerVo, Logger logger) throws ConvertException, IllegalAccessException, IOException {
        try {
            String crasServer = null;

            switch (schedulerVo.getType()) {
                case (ReqURLController.JOB_TYPE_REMOTEJOB):
                case (ReqURLController.JOB_TYPE_MANUALJOB):
                    crasServer = String.format(ReqURLController.API_DEFAULT_CRAS_SERVER_JOB,
                            schedulerVo.getRemoteJobVo().getSiteVoList().getCrasAddress(), schedulerVo.getRemoteJobVo().getSiteVoList().getCrasPort());
                    break;
                case (ReqURLController.JOB_TYPE_LOCALJOB):
                    Optional<ResSitesDetailDTO> siteVo = siteRepository.findBySiteId(schedulerVo.getLocalJobVo().getSiteId());
                    crasServer = String.format(ReqURLController.API_DEFAULT_CRAS_SERVER_JOB,
                            siteVo.get().getCrasAddress(), siteVo.get().getCrasPort());
                    break;
            }
            logger.info("crasServer Address : " + crasServer);

            HttpHeaders headers = new HttpHeaders();
            String clientIdType = schedulerVo.getType().equals(ReqURLController.JOB_TYPE_MANUALJOB) ? ReqURLController.JOB_TYPE_REMOTEJOB : schedulerVo.getType();
            headers.set(ReqURLController.JOB_CONTENT_TYPE, ReqURLController.JOB_APPLICATION_JSON);
            headers.set(ReqURLController.JOB_CLIENT_ID, clientIdType + "_" + String.format("%06d", schedulerVo.getJobId()));
            logger.info("client id : " + clientIdType + "_" + String.format("%06d", schedulerVo.getJobId()));

            CallRestAPI callRestAPI = new CallRestAPI();
            Object response = new Object();

            switch (schedulerVo.getStep()) {
                case (ReqURLController.JOB_STEP_COLLECT):
                    ReqJobRunDTO reqRunCollectDTO = new ReqJobRunDTO()
                            .setPlan_id(schedulerVo.getRemoteJobVo().getPlanIds());

                    HttpEntity<Object> requestCollect = new HttpEntity<>(reqRunCollectDTO, headers);

                    response = callRestAPI.postRestAPI(crasServer + ReqURLController.API_GET_POST_COLLECT,
                            requestCollect,
                            Object.class,
                            ReqURLController.API_GET_POST_COLLECT);
                    logger.info("Cras Server request URL : " + crasServer + ReqURLController.API_GET_POST_COLLECT);
                    logger.info("Request to execute collect step");
                    break;
                case (ReqURLController.JOB_STEP_CONVERT):
                    ReqJobRunDTO reqRunConvertDTO = new ReqJobRunDTO();

                    HttpEntity<Object> requestConvert = new HttpEntity<>(reqRunConvertDTO, headers);

                    response = callRestAPI.postRestAPI(crasServer + ReqURLController.API_GET_POST_CONVERT,
                            requestConvert,
                            Object.class,
                            ReqURLController.API_GET_POST_CONVERT);
                    logger.info("Cras Server request URL : " + crasServer + ReqURLController.API_GET_POST_CONVERT);
                    logger.info("Request to execute convert step");
                    break;
                case(ReqURLController.JOB_STEP_DBPURGE) :
                    ReqJobRunDTO reqRunPurgeDTO = new ReqJobRunDTO();

                    HttpEntity<Object> requestDbPurge = new HttpEntity<>(reqRunPurgeDTO, headers);

                    response = callRestAPI.postRestAPI(crasServer+ReqURLController.API_GET_POST_DBPURGE,
                            requestDbPurge,
                            Object.class,
                            ReqURLController.API_GET_POST_DBPURGE);
                    logger.info("Cras Server request URL : " + crasServer+ReqURLController.API_GET_POST_DBPURGE);
                    logger.info("Request to execute db purge step");
                    break;
                case (ReqURLController.JOB_STEP_CONVERT_LOCAL):
                    int[] fileIndices = schedulerVo.getLocalJobVo().getFileIndices();
                    List<String> fileNameList = new ArrayList<>();

                    for (int fileInx : fileIndices) {
                        Optional<LocalJobFileIdVo> localJobFileIdVo = localJobFileIdVoRepository.findById(fileInx);
                        fileNameList.add(uploadPath + "/" + localJobFileIdVo.get().getFileName());
                    }

                    MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();

                    for (String file : fileNameList)
                        form.add("files", new File(file));

                    form.add("data", "{}");

                    HttpHeaders headersLocal = new HttpHeaders();
                    headersLocal.set(ReqURLController.JOB_CONTENT_TYPE, ReqURLController.JOB_APPLICATION_FILE);
                    headersLocal.set(ReqURLController.JOB_CLIENT_ID, clientIdType + "_" + String.format("%06d", schedulerVo.getJobId()));
                    logger.info("client id : " + clientIdType + "_" + String.format("%06d", schedulerVo.getJobId()));

                    MultipartBodyBuilder builder = new MultipartBodyBuilder();
                    for (String file : fileNameList)
                        builder.part("files", new FileSystemResource(file));

                    builder.part("data", "{}").header(ReqURLController.JOB_CONTENT_TYPE, ReqURLController.JOB_APPLICATION_JSON);

                    MultiValueMap<String, HttpEntity<?>> body = builder.build();
                    HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity(body, headersLocal);

                    ResponseEntity<?> crasReturn = callRestAPI.postWithCustomHeaderMultipartFile(
                            crasServer + ReqURLController.API_GET_POST_CONVERT,
                            requestEntity,
                            Object.class);

                    response = crasReturn.getBody();

                    logger.info("Cras Server request URL : " + crasServer + ReqURLController.API_GET_POST_CONVERT);
                    logger.info("Request to execute local job convert step");
                    break;
            }
            return response;
        }
        catch (Exception e) {
            logger.error(e.toString());
            e.printStackTrace();
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            logger.error(errors.toString());
            throw e;
        }
    }
}

package jp.co.canon.rss.logmanager.scheduler;

import ch.qos.logback.classic.Logger;
import jp.co.canon.rss.logmanager.config.ReqURLController;
import jp.co.canon.rss.logmanager.dto.job.ReqNotiRunDTO;
import jp.co.canon.rss.logmanager.exception.ConvertException;
import jp.co.canon.rss.logmanager.util.CallRestAPI;
import jp.co.canon.rss.logmanager.vo.SchedulerVo;
import jp.co.canon.rss.logmanager.vo.crasdata.CrasItemMasterJobVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

@Component
public class NotiScheduler extends DynamicAbstractScheduler {
    @Override
    public Object runner(SchedulerVo schedulerVo, Logger logger) throws ConvertException {
        try {
            String crasServer = String.format(ReqURLController.API_DEFAULT_CRAS_SERVER_JOB,
                    schedulerVo.getRemoteJobVo().getSiteVoList().getCrasAddress(), schedulerVo.getRemoteJobVo().getSiteVoList().getCrasPort());
            logger.info("crasServer Address : " + crasServer);

            HttpHeaders headers = new HttpHeaders();
            String clientIdType = schedulerVo.getType().equals(ReqURLController.JOB_TYPE_MANUALJOB) ? ReqURLController.JOB_TYPE_REMOTEJOB : schedulerVo.getType();
            headers.set(ReqURLController.JOB_CONTENT_TYPE, ReqURLController.JOB_APPLICATION_JSON);
            headers.set(ReqURLController.JOB_CLIENT_ID, clientIdType + "_" + String.format("%06d", schedulerVo.getJobId()));
            logger.info("client id : " + clientIdType + "_" + String.format("%06d", schedulerVo.getJobId()));

            CallRestAPI callRestAPI = new CallRestAPI();
            Object response = new Object();

            switch (schedulerVo.getStep()) {
                case(ReqURLController.JOB_STEP_ERROR) :
                    ReqNotiRunDTO reqErrorNotiRunDTO = new ReqNotiRunDTO()
                            .setPeriod(schedulerVo.getRemoteJobVo().getMailContextVoErrorSummary().getBefore());

                    HttpEntity<Object> requestError = new HttpEntity<>(reqErrorNotiRunDTO, headers);

                    response = callRestAPI.postRestAPI(crasServer+ReqURLController.API_GET_POST_ERROR_SUMMARY,
                            requestError,
                            Object.class,
                            ReqURLController.API_GET_POST_ERROR_SUMMARY);
                    logger.info("Cras Server request URL : " + crasServer+ReqURLController.API_GET_POST_ERROR_SUMMARY);
                    logger.info("Request to execute error summary step");
                    break;
                case(ReqURLController.JOB_STEP_CRAS) :
                    List<CrasItemMasterJobVo> crasItemList = new ArrayList<>(schedulerVo.getRemoteJobVo().getMailContextVoCrasData().getCrasItem());

                    int rules[] = new int[crasItemList.size()];
                    int size = 0;
                    for (CrasItemMasterJobVo crasItemMasterJobVo : crasItemList) {
                        rules[size++] = crasItemMasterJobVo.getCrasRuleId();
                    }

                    ReqNotiRunDTO reqCrasNotiRunDTO = new ReqNotiRunDTO()
                            .setPeriod(schedulerVo.getRemoteJobVo().getMailContextVoCrasData().getBefore())
                            .setRule(rules);

                    HttpEntity<Object> requestCras = new HttpEntity<>(reqCrasNotiRunDTO, headers);

                    response = callRestAPI.postRestAPI(crasServer + ReqURLController.API_GET_POST_CRAS,
                            requestCras,
                            Object.class,
                            ReqURLController.API_GET_POST_CRAS);
                    logger.info("Cras Server request URL : " + crasServer + ReqURLController.API_GET_POST_CRAS);
                    logger.info("Request to execute cras step");
                    break;
                case(ReqURLController.JOB_STEP_VERSION) :
                    ReqNotiRunDTO reqVersionNotiRunDTO = new ReqNotiRunDTO()
                            .setPeriod(schedulerVo.getRemoteJobVo().getMailContextVoVersion().getBefore());

                    HttpEntity<Object> requestVersion = new HttpEntity<>(reqVersionNotiRunDTO, headers);

                    response = callRestAPI.postRestAPI(crasServer+ReqURLController.API_GET_POST_VERSION,
                            requestVersion,
                            Object.class,
                            ReqURLController.API_GET_POST_VERSION);
                    logger.info("Cras Server request URL : " + crasServer+ReqURLController.API_GET_POST_VERSION);
                    logger.info("Request to execute version step");
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

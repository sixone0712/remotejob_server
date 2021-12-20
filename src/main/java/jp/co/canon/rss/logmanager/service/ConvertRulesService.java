package jp.co.canon.rss.logmanager.service;

import jp.co.canon.rss.logmanager.config.ReqURLController;
import jp.co.canon.rss.logmanager.exception.ConvertException;
import jp.co.canon.rss.logmanager.util.CallRestAPI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
public class ConvertRulesService {
    @Value("${convert-data.cras-server-address}")
    private String crasServer;

    public Object getLogList(String path) throws ConvertException {
        try {
            CallRestAPI callRestAPI = new CallRestAPI();
            ResponseEntity<?> response = callRestAPI.getConvertRestAPI(crasServer+ReqURLController.API_GET_LOG_LIST_CRAS,
                    Object.class, path);
            return response.getBody();
        } catch (ConvertException e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    public Object getSpecificLog(int logId, String path) throws ConvertException {
        try {
            String API_GET_SPECIFIC_LOG_CRAS_URL = String.format(ReqURLController.API_GET_SPECIFIC_LOG_CRAS,
                    logId);

            CallRestAPI callRestAPI = new CallRestAPI();
            ResponseEntity<?> response = callRestAPI.getConvertRestAPI(crasServer+API_GET_SPECIFIC_LOG_CRAS_URL,
                    Object.class, path);
            return response.getBody();
        } catch (ConvertException e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    public Object addLog(Object reqAddLog, String path) throws ConvertException {
        try {
            CallRestAPI callRestAPI = new CallRestAPI();
            Object response = callRestAPI.postRestAPI(crasServer+ReqURLController.API_POST_ADD_LOG_CRAS,
                    reqAddLog,
                    Object.class, path);
            return response;
        } catch (ConvertException e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    public Object editLog(Object reqEditLog, int logId, String path) throws ConvertException {
        try {
            String API_PATCH_EDIT_LOG_CRAS_URL = String.format(ReqURLController.API_PATCH_EDIT_LOG_CRAS,
                    logId);

            CallRestAPI callRestAPI = new CallRestAPI();
            Object response = callRestAPI.patchRestAPI(crasServer+API_PATCH_EDIT_LOG_CRAS_URL,
                    reqEditLog,
                    Object.class, path);
            return response;
        } catch (ConvertException e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    public void deleteLog(int logId, String path) throws ConvertException {
        try {
            String API_DELETE_LOG_CRAS_URL = String.format(ReqURLController.API_DELETE_LOG_CRAS,
                    logId);

            CallRestAPI callRestAPI = new CallRestAPI();
            callRestAPI.deleteRestAPI(crasServer+API_DELETE_LOG_CRAS_URL, path);
        } catch (ConvertException e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    public Object getRuleList(int logId, String path) throws ConvertException {
        try {
            String API_GET_RULE_LIST_CRAS_URL = String.format(ReqURLController.API_GET_RULE_LIST_CRAS,
                    logId);

            CallRestAPI callRestAPI = new CallRestAPI();
            ResponseEntity<?> response = callRestAPI.getConvertRestAPI(crasServer+API_GET_RULE_LIST_CRAS_URL,
                    Object.class, path);
            return response.getBody();
        } catch (ConvertException e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    public Object getRuleOption(String path) throws ConvertException {
        try {
            CallRestAPI callRestAPI = new CallRestAPI();
            ResponseEntity<?> response = callRestAPI.getConvertRestAPI(crasServer+ReqURLController.API_GET_RULE_OPTION_CRAS,
                    Object.class, path);
            return response.getBody();
        } catch (ConvertException e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    public ResponseEntity<?> samplePreviewCsv(MultipartFile files, String path) throws ConvertException {
        try {
            CallRestAPI callRestAPI = new CallRestAPI();
            ResponseEntity<?> response = callRestAPI.postConvertPreviewMultipartFileRestAPI(
                    crasServer + ReqURLController.API_POST_SAMPLE_PREVIEW_CSV_CRAS,
                    files,
                    Object.class,
                    path);
            return response;
        } catch (ConvertException e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    public ResponseEntity<?> samplePreviewRegex(MultipartFile files, String path) throws ConvertException {
        try {
            CallRestAPI callRestAPI = new CallRestAPI();
            ResponseEntity<?> response = callRestAPI.postConvertPreviewMultipartFileRestAPI(
                    crasServer+ReqURLController.API_POST_SAMPLE_PREVIEW_REGEX_CRAS,
                    files,
                    Object.class,
                    path);
            return response;
        } catch (ConvertException e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    public Object getSpecificRule(int logId, int ruleId, String path) throws ConvertException {
        try {
            String API_GET_SPECIFIC_RULE_CARS_URL = String.format(ReqURLController.API_GET_SPECIFIC_RULE_CARS,
                    logId, ruleId);

            CallRestAPI callRestAPI = new CallRestAPI();
            ResponseEntity<?> response = callRestAPI.getConvertRestAPI(crasServer+API_GET_SPECIFIC_RULE_CARS_URL,
                    Object.class, path);
            return response.getBody();
        } catch (ConvertException e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    public ResponseEntity<?> getConvertPreviewCras(Object reqGetConvertPreview, String path) throws ConvertException {
        try {
            CallRestAPI callRestAPI = new CallRestAPI();
            ResponseEntity<?> response = callRestAPI.postConvertPreviewRestAPI(crasServer+ReqURLController.API_POST_CONVERT_PREVIEW_CSV_CRAS,
                    reqGetConvertPreview,
                    Object.class,
                    path);
            return response;
        } catch (ConvertException e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    public ResponseEntity<?> getConvertPreviewRegex(Object reqGetConvertPreview, String path) throws ConvertException {
        try {
            CallRestAPI callRestAPI = new CallRestAPI();
            ResponseEntity<?> response = callRestAPI.postConvertPreviewRestAPI(
                    crasServer + ReqURLController.API_POST_CONVERT_PREVIEW_REGEX_CRAS,
                    reqGetConvertPreview,
                    Object.class,
                    path);
            return response;
        } catch (ConvertException e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    public ResponseEntity<?> getFilterPreview(Object reqGetFilterPreview, String path) throws ConvertException {
        try {
            CallRestAPI callRestAPI = new CallRestAPI();
            ResponseEntity<?> response = callRestAPI.postConvertPreviewRestAPI(
                    crasServer + ReqURLController.API_POST_FILTER_PREVIEW_CRAS,
                    reqGetFilterPreview,
                    Object.class,
                    path);
            return response;
        } catch (ConvertException e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    public Object addConvertRule(Object reqAddConvertRule, int logId, String path) throws ConvertException {
        try {
            String API_POST_ADD_CONVERT_RULE_CRAS_URL = String.format(ReqURLController.API_POST_ADD_CONVERT_RULE_CRAS,
                    logId);

            CallRestAPI callRestAPI = new CallRestAPI();
            Object response = callRestAPI.postRestAPI(crasServer+API_POST_ADD_CONVERT_RULE_CRAS_URL,
                    reqAddConvertRule,
                    Object.class, path);
            return response;
        } catch (ConvertException e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    public ResponseEntity<?> editConvertRule(Object reqEditConvertRule, int logId, int ruleId, String path) throws ConvertException {
        try {
            String API_PATCH_EDIT_CONVERT_RULE_CRAS_URL = String.format(ReqURLController.API_PATCH_EDIT_CONVERT_RULE_CRAS,
                    logId, ruleId);

            CallRestAPI callRestAPI = new CallRestAPI();
            ResponseEntity<?> response = callRestAPI.patchRestAPI(crasServer+API_PATCH_EDIT_CONVERT_RULE_CRAS_URL,
                    reqEditConvertRule,
                    Object.class, path);
            return response;
        } catch (ConvertException e) {
            log.error(e.getMessage());
            throw e;
        }
    }
}

package jp.co.canon.rss.logmanager.config;

public class ReqURLController {
    // Address Controller URL
    public static final String API_DEFAULT_ADDRESS_URL = "/api/v1/address";
    public static final String API_GET_GROUP_ADD = "";
    public static final String API_GET_ADD = "/email";
    public static final String API_GET_GROUP_BY_EMAIL = "/email/{id}/group";
    public static final String API_DELETE_ADD = "/email";
    public static final String API_POST_ADD = "/email";
    public static final String API_PUT_ADD = "/email/{id}";
    public static final String API_GET_GROUP_NAME = "/group";
    public static final String API_GET_GROUP_EMAIL = "/group/{id}/email";
    public static final String API_POST_GROUP = "/group";
    public static final String API_PUT_GROUP = "/group/{id}";
    public static final String API_DELETE_GROUP = "/group/{id}";
    public static final String API_GET_SEARCH_ADD_GROUP = "/search";

    // Analysis Tool Api Controller URL
    public static final String API_DEFAULT_ANALYSIS_URL = "/api/v1/analysis";
    public static final String API_GET_EQUIPMENTS = "/equipments";
    public static final String API_GET_LOGLIST = "/loglist";
    public static final String API_GET_LOGDATA = "/date/{log_name}/{equipment}";
    public static final String API_GET_LOGMANAGER_CONNECTION = "/connection";
    public static final String API_GET_LOGTIME = "/log/{equipment}/{log_name}";

    // Auth Controller URL
    public static final String API_DEFAULT_AUTH_URL = "/api/v1/auth";
    public static final String API_GET_LOGIN = "/login";
    public static final String API_GET_LOGOUT = "/logout";
    public static final String API_POST_TOKEN = "/reissue";
    public static final String API_GET_ME = "/me";

    // Cras Server Download Controller URL
    public static final String API_DEFAULT_CRAS_SERVER_URL = "/api/cras";
    public static final String API_GET_DOWNLOAD_APP = "/app";

    // File Controller URL
    public static final String API_POST_UPLOADFILE = "/uploadFile";

    // History Controller URL
    public static final String API_DEFAULT_HISTORY_URL = "/api/v1/history/";
    public static final String API_GET_BUILD_LOG_LIST = "/{jobType}/{jobId}/{flag}";
    public static final String API_GET_BUILD_LOG_DETAIL_LOGMONITOR = "/{jobType}/{jobId}/{flag}/{id}/logmonitor";
    public static final String API_GET_BUILD_LOG_DETAIL_CRAS = "/{jobType}/{jobId}/{flag}/{id}/cras";

    // Host Controller URL
    public static final String API_DEFAULT_HOST_URL = "/api/v1/host";
    public static final String API_GET_SETTING_DB_INFO = "";

    // Job Controller URL
    public static final String API_DEFAULT_JOB_URL = "/api/v1/job";
    public static final String API_GET_REMOTE_JOB_DETAIL = "/remote/{id}";
    public static final String API_POST_NEW_REMOTE_JOB = "/remote";
    public static final String API_DELETE_REMOTE_JOB = "/remote/{id}";
    public static final String API_PUT_REMOTE_JOB = "/remote/{id}";
    public static final String API_GET_PLAN_LIST = "/remote/plan/{id}";
    public static final String API_GET_MANUAL = "/remote/manual/{id}/{step}";
    public static final String API_GET_REMOTE_JOB_STATUS = "/remote/{id}/status";
    public static final String API_PATCH_REMOTE_JOB_RUN = "/remote/{id}/run";
    public static final String API_PATCH_REMOTE_JOB_STOP = "/remote/{id}/stop";
    public static final String API_GET_TIME_LINE = "/remote/timeline/{id}";
    public static final String API_POST_NEW_LOCAL_JOB = "/local";
    public static final String API_DELETE_LOCAL_JOB = "/local/{id}";

    // Running Job Define
    public static final String JOB_THREAD_JOB = "job";
    public static final String JOB_THREAD_NOTI = "noti";

    public static final String JOB_TYPE_REMOTEJOB = "remote";
    public static final String JOB_TYPE_LOCALJOB = "local";
    public static final String JOB_TYPE_MANUALJOB = "manual";

    public static final String JOB_STEP_COLLECT = "collect";
    public static final String JOB_STEP_CONVERT = "convert";
    public static final String JOB_STEP_CONVERT_LOCAL = "convert_local";
    public static final String JOB_STEP_ERROR = "error";
    public static final String JOB_STEP_SUMMARY_CRAS = "summary";
    public static final String JOB_STEP_CRAS = "cras";
    public static final String JOB_STEP_VERSION = "version";
    public static final String JOB_STEP_DBPURGE = "purge";
    public static final String JOB_STEP_ERR_NOTICE = "errNoti";

    public static final String JOB_RUN_CYCLE = "cycle";
    public static final String JOB_RUN_TIME = "time";
    public static final String JOB_CYCLE_DAY = "day";
    public static final String JOB_CYCLE_HOUR = "hour";
    public static final String JOB_CYCLE_MINUTE = "minute";

    public static final String JOB_STATUS_SUCCESS = "success";
    public static final String JOB_STATUS_FAILURE = "failure";
    public static final String JOB_STATUS_NOTBUILD = "notbuild";
    public static final String JOB_STATUS_PROCESSING = "processing";
    public static final String JOB_STATUS_NODATA = "nodata";
    public static final String JOB_STATUS_TIMEOUT = "timeout";

    public static final String JOB_NOT_SETTING = "No settings";

    public static final String JOB_STATUS_CRAS_SUCCESS = "success";
    public static final String JOB_STATUS_CRAS_ERROR = "error";
    public static final String JOB_STATUS_CRAS_CANCEL = "cancel";
    //TODO : idle로 변경 필요
    public static final String JOB_STATUS_CRAS_IDLE = "unknown";
    public static final String JOB_STATUS_CRAS_RUNNING = "running";

    public static final int JOB_PLUS_SECOND = 5;

    public static final String JOB_CONTENT_TYPE = "Content-Type";
    public static final String JOB_APPLICATION_JSON = "application/json";
    public static final String JOB_APPLICATION_FILE = "multipart/form-data";
    public static final String JOB_CLIENT_ID = "client-id";

    public static final String JOB_SCRIPT_PRE = "pre";
    public static final String JOB_SCRIPT_POST = "post";

    public static final int JOB_TIMELINE = 50;

    // Call Cras Server : Running Job
    public static final String API_DEFAULT_CRAS_SERVER_JOB = "http://%s:%s/api/v1/s";
    public static final String API_GET_POST_COLLECT = "/collect/job";
    public static final String API_GET_POST_CONVERT = "/convert/job";
    public static final String API_GET_POST_ERROR_SUMMARY = "/summary/job";
    public static final String API_GET_POST_CRAS = "/cras/job";
    public static final String API_GET_POST_VERSION = "/version/job";
    public static final String API_GET_POST_DBPURGE = "/purge/job";
    public static final String API_POST_SET_SCRIPT = "/%s/script";
    public static final String API_GET_GET_SCRIPT = "/%s/script/%s";

    // Status Controller URL
    public static final String API_DEFAULT_STATUS_URL = "/api/v1/status";
    public static final String API_GET_REMOTE_JOB_LIST = "/job/remote";
    public static final String API_GET_LOCAL_JOB_LIST = "/job/local";
    public static final String API_GET_SITE_NAME_ALL = "/site";
    public static final String API_GET_SITE_NAME_NOTADDED = "/site/job";
    public static final String API_GET_CRAS_DATA_SITE_INFO = "/site/cras";

    // Site Controller URL
    public static final String API_DEFAULT_SITE_URL = "/api/v1/site";
    public static final String API_GET_ALL_SITE_LIST = "";
    public static final String API_GET_SITE_DETAIL = "/{id}";
    public static final String API_POST_ADD_NEW_SITE = "";
    public static final String API_PUT_SITE_INFO = "/{id}";
    public static final String API_GET_JOB_STATUS = "/{siteId}/jobstatus";
    public static final String API_DEL_SITE = "/{id}";
    public static final String API_POST_CRAS_CONNECTION = "/connection/cras";
    public static final String API_POST_RSS_CONNECTION = "/connection/rss";
    public static final String API_POST_EMAIL_CONNECTION = "/connection/email";

    // Upload Controller URL
    public static final String API_DEFAULT_UPLOAD_URL = "/api/v1/upload";
    public static final String API_POST_LOCALFILE = "";
    public static final String API_GET_CRASDATAFILE = "/crasdatafile/{id}";
    public static final String API_POST_CRASDATAFILE = "/crasdatafile/{id}";
    public static final String API_GET_ERRORLOG_DOWNLOAD = "/errorlog/{id}";
    public static final String API_POST_ERRORLOG_DOWNLOAD = "/errorlog/{id}";

    // User Controller URL ReqURLController
    public static final String API_DEFAULT_USER_URL = "/api/v1/user";
    public static final String API_GET_USERS = "";
    public static final String API_POST_SIGNUP = "";
    public static final String API_DELETE_USER = "/{id}";
    public static final String API_PUT_CHANGE_ROLES = "/{id}/roles";
    public static final String API_PUT_CHANGE_PASSWORD = "/{id}/password";

    // Version Controller URL ReqURLController
    public static final String API_DEFAULT_VERSION_URL = "/api/v1/version";
    public static final String API_GET_SERVER_VERSION = "";

    // Rule CRAS DATA Controller URL
    public static final String API_DEFAULT_RULE_CRAS_URL = "/api/v1/rule/cras";
    public static final String API_GET_CRAS_DATA_SITE = "";
    public static final String API_POST_CRAS_DATA_SITE = "";
    public static final String API_DELETE_CRAS_DATA_SITE = "/{id}";

    public static final String API_POST_CRAS_DATA = "/{id}/create";
    public static final String API_GET_CRAS_DATA = "/{id}/create";
    public static final String API_GET_CRAS_DATA_DETAIL = "/{id}/create/{itemId}";
    public static final String API_PUT_CRAS_DATA = "/{id}/create/{itemId}";
    public static final String API_DELETE_CRAS_DATA = "/{id}/create/{itemId}";

    public static final String API_POST_JUDGE_RULE = "/{id}/judge";
    public static final String API_GET_JUDGE_RULES = "/{id}/judge";
    public static final String API_GET_JUDGE_RULES_ENABLE = "/{id}/judge/enable";
    public static final String API_GET_JUDGE_RULE_DETAIL = "/{id}/judge/{itemId}";
    public static final String API_PUT_JUDGE_RULE_DETAIL = "/{id}/judge/{itemId}";
    public static final String API_DELETE_JUDGE_RULE_DETAIL = "/{id}/judge/{itemId}";

    public static final String API_GET_OPTION_LIST_DETAIL = "/option/{cras}";

    public static final String API_GET_TABLE_LIST_DETAIL = "/{id}/table";
    public static final String API_GET_COLUMN_LIST_DETAIL = "/{id}/table/{tableName}";

    public static final String API_POST_TEST_QUERY = "/testquery";

    // Rule Convert Controller URL
    public static final String API_DEFAULT_RULE_CONVERT_URL = "/api/v1/rule/convert";
    public static final String API_GET_LOG_LIST = "/log";
    public static final String API_GET_SPECIFIC_LOG_LIST = "/log/{logId}";
    public static final String API_POST_ADD_LOG = "/log";
    public static final String API_PATCH_EDIT_LOG = "/log/{logId}";
    public static final String API_DELETE_LOG = "/log/{logId}";
    public static final String API_GET_RULE_LIST = "/log/{logId}/rule";
    public static final String API_GET_RULE_OPTION = "/option";
    public static final String API_GET_SPECIFIC_RULE = "/log/{logId}/rule/{ruleId}";
    public static final String API_POST_SAMPLE_PREVIEW_CSV = "/log/preview/csv";
    public static final String API_POST_SAMPLE_PREVIEW_REGEX = "/log/preview/regex";
    public static final String API_POST_CONVERT_PREVIEW_CSV = "/log/rule/preview/csv";
    public static final String API_POST_CONVERT_PREVIEW_REGEX = "/log/rule/preview/regex";
    public static final String API_POST_FILTER_PREVIEW = "/log/filter/preview";
    public static final String API_POST_ADD_CONVERT_RULE = "/log/{logId}/rule";
    public static final String API_PUT_EDIT_CONVERT_RULE = "/log/{logId}/rule/{ruleId}";
    public static final String API_GET_CONVERTRULESFILE = "/export";
    public static final String API_POST_CONVERTRULESFILE = "/import";

    // Backup Controller URL ReqURLController
    public static final String API_DEFAULT_DEVLOG_URL = "/api/v1/backup";
    public static final String API_GET_LOGMONITOR = "/logmonitor";
    public static final String API_GET_CRASSERVER = "/crasserver/{siteId}";

    // Error Log Download Controller URL
    public static final String API_DEFAULT_ERROR_LOG_URL = "/api/v1/errorlog";
    public static final String API_GET_ERROR_LOG_LIST = "/{id}";
    public static final String API_GET_ERROR_LOG_SETTING_LIST = "/setting/{id}";
    public static final String API_POST_DOWNLOAD_REQ = "/download/{id}";
    public static final String API_GET_DOWNLOAD_LIST = "/download/{id}";
    public static final String API_GET_DOWNLOAD_FILE = "/download/file/{id}";

    // Error Log Download Define
    public static final String DOWNLOAD_FTP = "FTP";
    public static final String DOWNLOAD_VFTP_COMPAT = "VFTP(COMPAT)";
    public static final String DOWNLOAD_VFTP_SSS = "VFTP(SSS)";
    public static final String DOWNLOAD_FTP_CRAS = "ftp";
    public static final String DOWNLOAD_VFTP_COMPAT_CRAS = "vftp_compat";
    public static final String DOWNLOAD_VFTP_SSS_CRAS = "vftp_sss";

    // Call Cras Server : Error Log Download
    public static final String API_DEFAULT_CRAS_SERVER_ERRORLOG = "http://%s:%s/api/v1/errorlog";
    public static final String API_DEFAULT_CRAS_SERVER_ERRORLOG_DOWNLOAD = "http://%s:%s";
    public static final String API_GET_ERRORLOG_LIST = "/list";
    public static final String API_POST_ERRORLOG_CRAS_DOWNLOAD = "/download";
    public static final String API_GET_ERRORLOG_CRAS_DOWNLOAD = "/download/";

    // Call Cras Server : AnalysisToolApiController
    public static final String API_GET_ALL_MPA_LIST = "http://%s:%s/api/rapid/equipment?&fab=%s";
    public static final String API_GET_LOG_DATA_TIME = "http://%s:%s/api/convert/log/%s?equipment=%s";
    public static final String API_GET_LOG_DATA = "http://%s:%s/api/convert/log/dump/%s?start=%s&end=%s&equipment=%s";

    // Call Cras Server : ConfingureController
    public static final String API_GET_PLAN_LIST_FROM_CRAS = "http://%s:%s/api/rapid/plan?host=%s&port=%s&user=%s&pass=%s";
    public static final String API_GET_CRAS_CONNECTION = "http://%s:%s/api";
    public static final String API_GET_RSS_CONNECTION = "http://%s:%d/api/rapid/valid?host=%s&port=%d&user=%s&password=%s";
    public static final String API_POST_USERNAME_INFO_CRAS = "http://%s:%d/api/v1/sys/config/username";
    public static final String API_POST_RAPID_INFO_CRAS = "http://%s:%d/api/v1/sys/config/rapid";

    // Call Cras Server : Job Controller URL
    public static final String API_GET_BUILD_LOG_LIST_DETAIL = "http://%s/api/v1/s/%s/log/%s";
    public static final String API_GET_POST_SCRIPT = "http://%s/api/v1/s/collect/script";

    // Call Cras Server : Rule CRAS DATA Controller URL
    public static final String API_GET_CRAS_LEGACY_DATA = "http://%s:%s/api/cras/legacy/download";

    public static final String API_GET_CONVERT_TABLE_LIST = "http://%s:%s/api/v1/convert/table?fab=%d";
    public static final String API_POST_TEST_QUERY_TO_CRAS = "http://%s:%s/api/v1/convert/query";

    // Call Cras Server : Rule Convert Controller URL
    public static final String API_GET_LOG_LIST_CRAS = "/api/v1/convert/log";
    public static final String API_GET_SPECIFIC_LOG_CRAS = "/api/v1/convert/log/%d";
    public static final String API_POST_ADD_LOG_CRAS = "/api/v1/convert/log";
    public static final String API_PATCH_EDIT_LOG_CRAS = "/api/v1/convert/log/%d";
    public static final String API_DELETE_LOG_CRAS = "/api/v1/convert/log/%d";
    public static final String API_GET_RULE_LIST_CRAS = "/api/v1/convert/log/%d/rule";
    public static final String API_GET_RULE_OPTION_CRAS = "/api/v1/convert/option";
    public static final String API_GET_SPECIFIC_RULE_CARS = "/api/v1/convert/log/%d/rule/%d";
    public static final String API_POST_SAMPLE_PREVIEW_CSV_CRAS = "/api/v1/convert/log/preview/csv";
    public static final String API_POST_SAMPLE_PREVIEW_REGEX_CRAS = "/api/v1/convert/log/preview/regex";
    public static final String API_POST_CONVERT_PREVIEW_CSV_CRAS = "/api/v1/convert/log/rule/preview/csv";
    public static final String API_POST_CONVERT_PREVIEW_REGEX_CRAS = "/api/v1/convert/log/rule/preview/regex";
    public static final String API_POST_FILTER_PREVIEW_CRAS = "/api/v1/convert/log/filter/preview";
    public static final String API_POST_ADD_CONVERT_RULE_CRAS = "/api/v1/convert/log/%d/rule";
    public static final String API_PATCH_EDIT_CONVERT_RULE_CRAS = "/api/v1/convert/log/%d/rule/%d";

    public static final String API_GET_CONVERTRULESFILE_CRAS = "/api/v1/convert/export";
    public static final String API_POST_CONVERTRULESFILE_CRAS = "/api/v1/convert/import";

    // Call Cras Server : Download Dev Log
    public static final String API_GET_CRAS_DEV_LOG = "http://%s:%s/api/cras/job/download/debug?";
}

package jp.co.canon.rss.logmanager.manager;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Setter
@Getter
public class StatusDetail {
    public static final String STATUS_IDLE = "idle";
    public static final String STATUS_RUNNING = "running";
    public static final String STATUS_ERROR = "error";
    public static final String STATUS_SUCCESS = "success";
    public static final String STATUS_CANCEL = "cancel";
    public static final String DETAIL_NONE = "";

    private String status;
    private String detail;

    public void setStatus(String status, String detail) {
        this.status = status;
        this.detail = detail;

        if(status.equals("STATUS_ERROR")) {
            log.error(detail);
        }
    }
}

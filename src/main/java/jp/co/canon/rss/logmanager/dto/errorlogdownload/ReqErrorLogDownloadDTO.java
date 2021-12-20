package jp.co.canon.rss.logmanager.dto.errorlogdownload;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ReqErrorLogDownloadDTO {
    public int siteId;
    public String error_code;
    public String occurred_date;
    public String type;
    public String command;
    public String start;
    public String end;
    public String equipment_name;
    public String device;
    public String process;
}

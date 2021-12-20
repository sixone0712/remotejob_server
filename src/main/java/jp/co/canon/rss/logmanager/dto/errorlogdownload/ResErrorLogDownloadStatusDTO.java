package jp.co.canon.rss.logmanager.dto.errorlogdownload;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.Column;

@Getter
@Setter
@Accessors(chain = true)
public class ResErrorLogDownloadStatusDTO {
    public int siteId;
    public String error_code;
    public String occurred_date;
    public String equipment_name;
    public String type;
    public String command;
    public String start;
    public String end;
    public String status;
    public String error;
    public String device;
    public String process;
    public String download_url;
    public String download_id;
}

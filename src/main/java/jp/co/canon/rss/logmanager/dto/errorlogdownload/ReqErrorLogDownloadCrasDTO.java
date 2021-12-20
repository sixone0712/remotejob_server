package jp.co.canon.rss.logmanager.dto.errorlogdownload;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ReqErrorLogDownloadCrasDTO {
    public String ftp_type;
    public String start_date;
    public String end_date;
    public String [] machine;
    public String [] command;
    public String device;
    public String process;
}

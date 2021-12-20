package jp.co.canon.rss.logmanager.dto.errorlogdownload;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ResErrorLogDownloadStatusCrasDTO {
    public String client;
    public String created;
    public String [] download_url;
    public String id;
    public String status;
    public String step;
    public String [] error;
}

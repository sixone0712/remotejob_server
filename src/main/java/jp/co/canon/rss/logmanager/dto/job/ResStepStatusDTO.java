package jp.co.canon.rss.logmanager.dto.job;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ResStepStatusDTO {
    private String id;
    private String client;
    private String step;
    private String created;
    private String status;
    private String [] error;
    private String [] download_url;
}

package jp.co.canon.rss.logmanager.dto.job;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ResTimeLineDTO {
    private String start;
    private String runningStart;
    private String end;
    private String name;
    private String status;
    private Boolean isManual;
    private String logId;
}

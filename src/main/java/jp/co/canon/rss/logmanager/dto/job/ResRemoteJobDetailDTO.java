package jp.co.canon.rss.logmanager.dto.job;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ResRemoteJobDetailDTO {
    private int siteId;
    private String siteName;
    private int jobId;
    private String jobName;
    private int [] planIds;

    private Boolean isConvert;
    private Boolean isErrorSummary;
    private Boolean isCrasData;
    private Boolean isMpaVersion;
    private Boolean isDbPurge;
    private Boolean isErrorNotice;

    private ResMailContextDTO collect;
    private ResMailContextDTO convert;
    private ResMailContextDTO errorSummary;
    private ResMailContextDTO crasData;
    private ResMailContextDTO mpaVersion;
    private ResMailContextDTO dbPurge;
    private ResMailContextDTO errorNotice;
}

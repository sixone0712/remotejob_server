package jp.co.canon.rss.logmanager.dto.job;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ResRemoteJobDetailAddDTO {
    private String jobName;
    private int siteId;
    private int [] planIds;

    private Boolean isConvert;
    private Boolean isErrorSummary;
    private Boolean isCrasData;
    private Boolean isMpaVersion;
    private Boolean isDbPurge;
    private Boolean isErrorNotice;

    private ResMailContextAddDTO collect;
    private ResMailContextAddDTO convert;
    private ResMailContextAddDTO errorSummary;
    private ResMailContextAddDTO crasData;
    private ResMailContextAddDTO mpaVersion;
    private ResMailContextAddDTO dbPurge;
    private ResMailContextAddDTO errorNotice;
}

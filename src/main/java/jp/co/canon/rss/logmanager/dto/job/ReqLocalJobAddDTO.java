package jp.co.canon.rss.logmanager.dto.job;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ReqLocalJobAddDTO {
    private Integer siteId;
    private int [] fileIndices;
    private Boolean isErrorNotice;
    private ResMailContextAddDTO errorNotice;

    public ReqLocalJobAddDTO(Integer siteId, int [] fileIndices,
                             Boolean isErrorNotice, ResMailContextAddDTO errorNotice) {
        this.siteId = siteId;
        this.fileIndices = fileIndices;
        this.isErrorNotice = isErrorNotice;
        this.errorNotice = errorNotice;
    }
}

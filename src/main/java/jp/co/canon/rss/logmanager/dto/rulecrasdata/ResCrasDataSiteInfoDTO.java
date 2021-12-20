package jp.co.canon.rss.logmanager.dto.rulecrasdata;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Schema
@Accessors(chain = true)
public class ResCrasDataSiteInfoDTO {
    private int siteId;
    private String name;
}

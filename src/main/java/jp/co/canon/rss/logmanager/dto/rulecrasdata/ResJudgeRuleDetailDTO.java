package jp.co.canon.rss.logmanager.dto.rulecrasdata;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Schema
@Accessors(chain = true)
public class ResJudgeRuleDetailDTO {
    private int itemId;
    private String itemName;
    private String title;
    private String description;
    private double calRange;
    private String calCondition;
    private double threshold;
    private String compare;
    private Boolean enable;
}

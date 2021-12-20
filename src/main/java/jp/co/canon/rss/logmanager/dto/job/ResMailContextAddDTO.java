package jp.co.canon.rss.logmanager.dto.job;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ResMailContextAddDTO {
    private String subject;
    private String body;
    private int before;

    private String mode;
    private String [] time;
    private String cycle;
    private int period;

    private String [] customEmails;
    private long [] emailBookIds;
    private long [] groupBookIds;

    private ResMailContextScriptDTO script;
    private int [] selectJudgeRules;
}

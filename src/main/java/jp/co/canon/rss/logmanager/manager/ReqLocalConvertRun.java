package jp.co.canon.rss.logmanager.manager;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class ReqLocalConvertRun {
    private String source;
    private ReqConvertRunConfig config;
    private Long file[];
    private String company;
    private String fab;
    private String job_id;
}

package jp.co.canon.rss.logmanager.manager;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class ReqRemoteConvertRun {
    private String source;
    private ReqConvertRunConfig config;
    private int before;
    private int plan_id[];
    private String created;
    private String company;
    private String fab;
    private String job_id;
}

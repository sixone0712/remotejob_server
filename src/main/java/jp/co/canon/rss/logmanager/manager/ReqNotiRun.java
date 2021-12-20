package jp.co.canon.rss.logmanager.manager;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class ReqNotiRun {
    private String company;
    private String fab;
    private int before;
    private String job_id;
}

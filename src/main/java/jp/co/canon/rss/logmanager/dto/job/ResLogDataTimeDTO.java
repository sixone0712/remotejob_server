package jp.co.canon.rss.logmanager.dto.job;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ResLogDataTimeDTO {
    public String end;
    public int count;
    public String start;
}

package jp.co.canon.rss.logmanager.dto.analysis;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ResLogTimeDTO {
    public int count;
    public String end;
    public String start;
}

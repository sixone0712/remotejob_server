package jp.co.canon.rss.logmanager.dto.history;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ResHistoryDTO {
    public String id;
    public String name;
    public String status;
}

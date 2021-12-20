package jp.co.canon.rss.logmanager.dto.history;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ReqHistoryDTO {
    public String id;
    public String to_char;
    public String status;
    public String start;
    public String job_type;
}

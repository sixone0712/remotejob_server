package jp.co.canon.rss.logmanager.dto.job;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.util.MultiValueMap;

@Getter
@Setter
@Accessors(chain = true)
public class ReqLocalJobRunDTO {
    private MultiValueMap<String, Object> files;
    private String data;
}

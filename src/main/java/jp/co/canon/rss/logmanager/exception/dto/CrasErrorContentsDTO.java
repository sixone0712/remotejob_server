package jp.co.canon.rss.logmanager.exception.dto;

import lombok.*;
import lombok.experimental.Accessors;

import java.util.Map;

@Getter
@Setter
@ToString
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class CrasErrorContentsDTO {
    private Map<String, Object> cras_error;
}

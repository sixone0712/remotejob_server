package jp.co.canon.rss.logmanager.exception.dto;

import lombok.*;
import lombok.experimental.Accessors;

@Getter
@Setter
@ToString
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class CrasErrorDTO {
    private Object path;
    private Object error;
    private Object error_list;
}

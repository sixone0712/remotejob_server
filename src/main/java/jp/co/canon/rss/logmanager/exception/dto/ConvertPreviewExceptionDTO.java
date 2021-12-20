package jp.co.canon.rss.logmanager.exception.dto;

import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class ConvertPreviewExceptionDTO {
    private LocalDateTime timestamp;
    private int status;
    private CrasErrorDTO cras_error;
    private String message;
    private String path;
}

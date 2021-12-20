package jp.co.canon.rss.logmanager.exception;

import jp.co.canon.rss.logmanager.exception.dto.CrasErrorDTO;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ConvertException extends Exception {
    LocalDateTime timestamp;
    int status;
    CrasErrorDTO crasError;
    String message;
    String path;

    public ConvertException(LocalDateTime timestamp, int status, CrasErrorDTO crasError, String message, String path){
        this.message = message;
        this.timestamp = timestamp;
        this.status = status;
        this.crasError = crasError;
        this.path = path;
    }
}

package jp.co.canon.rss.logmanager.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class StatusResourceNotFoundException extends Exception{

    private static final long serialVersionUID = 1L;

    public StatusResourceNotFoundException(String message){
        log.error(String.format("[ERR] %s", message));
    }
}

package jp.co.canon.rss.logmanager.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class ConnectionFailException extends Exception{
    public ConnectionFailException(){
        log.error(String.format("[ERR] Fail to connect with server."));
    }
}

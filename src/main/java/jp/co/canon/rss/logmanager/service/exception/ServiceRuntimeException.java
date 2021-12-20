package jp.co.canon.rss.logmanager.service.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ServiceRuntimeException extends RuntimeException {

    public ServiceRuntimeException(String message) {
        super(message);
    }
}

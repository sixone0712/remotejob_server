package jp.co.canon.rss.logmanager.service.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ServiceException extends Exception {

    public ServiceException(String message) {
        super(message);
    }
}

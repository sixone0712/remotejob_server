package jp.co.canon.rss.logmanager.service.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ServiceInvalidParameterException extends ServiceException {

    public ServiceInvalidParameterException(String message) {
        super(message);
    }
}

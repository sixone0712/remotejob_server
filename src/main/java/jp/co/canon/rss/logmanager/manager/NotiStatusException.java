package jp.co.canon.rss.logmanager.manager;

public class NotiStatusException extends Throwable {
    public enum Error {
        invalidParam,
        jobFull,
        searchFail,
        unknown
    }

    public NotiStatusException(NotiStatusException.Error error) {
        super(error.name());
    }
}

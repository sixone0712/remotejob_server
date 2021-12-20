package jp.co.canon.rss.logmanager.manager;

public class JobStatusException extends Throwable {
    public enum Error {
        invalidParam,
        jobFull,
        searchFail,
        unknown
    }

    public JobStatusException(Error error) {
        super(error.name());
    }
}

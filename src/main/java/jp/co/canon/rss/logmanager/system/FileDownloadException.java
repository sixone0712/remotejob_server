package jp.co.canon.rss.logmanager.system;

public class FileDownloadException extends Throwable {
    public FileDownloadException(String message) {
        super(message);
    }

    public FileDownloadException(String message, Throwable cause) {
        super(message, cause);
    }
}

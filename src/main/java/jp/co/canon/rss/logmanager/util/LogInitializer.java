package jp.co.canon.rss.logmanager.util;

public interface LogInitializer {
    String getLogType();
    String getPattern();
    String getFileName();
}

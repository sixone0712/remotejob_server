package jp.co.canon.rss.logmanager.util;

public class JobSchedulerLogInitializer implements LogInitializer {
    @Override
    public String getLogType() {
        return LogType.scheduler.name();
    }

    @Override
    public String getPattern() {
        return "%r %thread %level - %msg%n";
    }

    @Override
    public String getFileName() {
        return "control.log";
    }
}

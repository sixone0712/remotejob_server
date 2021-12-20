package jp.co.canon.rss.logmanager.util;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.core.FileAppender;
import lombok.Getter;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class LogMonitorLogFactory {

    @Value("${logmonitor.logging.root}")
    @Getter
    private String root;

    private Map<LogType, Logger> logs;

    private Map<String, FileLog> fileLogs;

    private LogInitializer[] initializers = {
            new JobSchedulerLogInitializer()
    };

    @Getter
    static LogMonitorLogFactory instance;

    @PostConstruct
    private void _init() {
        if(root==null || root.isEmpty()) {
            throw new RuntimeException("failed to initialize LogFactory");
        }

        logs = new HashMap<>();
        for(LogType type: LogType.values()) {
            logs.put(type, createLogger(type));
            //printTest(logs.get(type));
        }
        if(instance!=null) {
            throw new RuntimeException("duplicated LogFactory error");
        }
        instance = this;

        fileLogs = new HashMap<>();

        Logger console = (Logger) LoggerFactory.getLogger("consoleLogger");
        console.info("disable root logger");
//        console.setLevel(Level.OFF);
        console.setLevel(Level.ALL);
    }

    private void printTest(Logger log) {
        if(log!=null) {
            log.info("test print");
        }
    }

    private Logger createLogger(LogType type) {

        Path path = Paths.get(root, type.name());
        File dir = path.toFile();

        if(!dir.exists()) {
            dir.mkdirs();
        } else if(dir.isFile()) {
            dir.delete();
        }

        LogInitializer initializer = getInitializer(type);

        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        FileAppender appender = new FileAppender();

        appender.setContext(context);
        appender.setName(type.name());
        appender.setFile(dir.getPath()+"/"+initializer.getFileName());

        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(context);
        encoder.setPattern(initializer.getPattern());
        encoder.start();

        appender.setEncoder(encoder);
        appender.start();

        Logger log = context.getLogger(type.name());
        log.addAppender(appender);
        log.setAdditive(false);

        return log;
    }

    private LogInitializer getInitializer(LogType type) {
        String _type = type.name();
        for(LogInitializer init: initializers) {
            if(init.getLogType().equals(_type)) {
                return init;
            }
        }
        throw new RuntimeException("couldn't find "+type.name()+" log initializer");
    }

    private boolean existInstanceLogger(String name) {
        if(name!=null && fileLogs.containsKey(name)) {
            return true;
        }
        return false;
    }

    private FileLog getExistInstanceLogger(String name) {
        if(name!=null && existInstanceLogger(name)) {
            return fileLogs.get(name);
        }
        return null;
    }

    public static Logger getLogger(LogType type) {

        LogMonitorLogFactory factory = LogMonitorLogFactory.getInstance();
        return factory.logs.get(type);
    }

    public static FileLog getFileLogger(String name) {

        LogMonitorLogFactory factory = LogMonitorLogFactory.getInstance();

        FileLog fileLog = factory.getExistInstanceLogger(name);
        if(fileLog!=null && fileLog.getLogger()!=null) {
            return fileLog;
        }

        fileLog = new FileLog(factory.root, name);
        factory.fileLogs.put(name, fileLog);
        return fileLog;
    }
}

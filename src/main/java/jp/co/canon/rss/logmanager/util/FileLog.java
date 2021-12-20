package jp.co.canon.rss.logmanager.util;


import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Function;

public class FileLog extends LogMonitorLog {

    private static final String downloadRoot = "jobs";

    @Getter
    private final String name;

    @Getter
    private File file;

    @Getter
    private String fileName;

    @Getter
    private Logger logger;

    @Getter
    private String pattern;

    @Getter
    private boolean useRollingAppender;

    @Getter
    private String fileNamePattern;

    @Getter
    private Function<String, String> getPatternedFileName;

    @Getter
    private int maxHistory;

    public FileLog(File file, String name, String pattern, boolean useRollingAppender,
                   String fileNamePattern, int maxHistory, Function generateFileNamePattern) {

        super(name);
        this.name = name;
        this.file = file;
        this.useRollingAppender = useRollingAppender;
        this.maxHistory = maxHistory;
        this.pattern = pattern;
        this.fileNamePattern = fileNamePattern;
        this.getPatternedFileName = generateFileNamePattern;

        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(context);
        encoder.setPattern(this.pattern);
        encoder.start();

        Logger log = context.getLogger(name);

        if(useRollingAppender) {
            RollingFileAppender appender = new RollingFileAppender();
            appender.setContext(context);
            appender.setName(this.name);
            appender.setFile(this.file.toString());
            appender.setEncoder(encoder);
            appender.setAppend(true);

            TimeBasedRollingPolicy policy = new TimeBasedRollingPolicy();
            policy.setContext(context);
            policy.setParent(appender);
            policy.setMaxHistory(this.maxHistory);
            policy.setFileNamePattern(this.getPatternedFileName.apply(file.toString()));
            policy.start();

            appender.setRollingPolicy(policy);
            appender.start();

            log.addAppender(appender);
        } else {
            FileAppender appender = new FileAppender();
            appender.setContext(context);
            appender.setName(this.name);
            appender.setFile(this.file.toString());
            appender.setEncoder(encoder);
            appender.start();

            log.addAppender(appender);
        }

        log.setAdditive(false);
        this.logger = log;
    }

    public FileLog(String root, String name) {

        super(name);

        this.name = name;

        // Check debugging root.
        File rootFile = Paths.get(root, downloadRoot).toFile();

        if(rootFile.isFile()) {
            rootFile.delete();
        }

        if(!rootFile.exists()) {
            rootFile.mkdirs();
        }

        // Create file name
        fileName = String.format("%s.log", name);

        this.logger = createLogger(Paths.get(rootFile.toString(), fileName).toFile());

        printStamp();
    }

    public FileLog(File file, String name) {

        super(name);

        this.name = name;
        this.file = file;
        this.fileName = file.getName();

        doFileValidity(file);

        this.logger = createLogger(file);

        printStamp();
    }

    public FileLog(File file, String name, boolean rolling) {

        super(name);

        this.name = name;

        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(context);
        encoder.setPattern("%date{yyyy-MM-dd HH:mm:ss.SSS} [%-20class{0}][%-15thread] %-6level %msg%n");
        encoder.start();

        RollingFileAppender appender = new RollingFileAppender();
        appender.setContext(context);
        appender.setName(file.getName());
        appender.setFile(file.toString());
        appender.setEncoder(encoder);
        appender.setAppend(true);

        TimeBasedRollingPolicy rollingPolicy = new TimeBasedRollingPolicy();
        rollingPolicy.setContext(context);
        rollingPolicy.setParent(appender);
        rollingPolicy.setMaxHistory(7);
        rollingPolicy.setFileNamePattern(file.toString()+".%d{yyyy-MM-dd}.%i");
        rollingPolicy.start();

        appender.setRollingPolicy(rollingPolicy);
        appender.start();

        Logger log = context.getLogger(name);
        log.addAppender(appender);
        log.setAdditive(false);
        this.logger = log;

        printStamp();
    }

    @Setter
    @Accessors(chain = true, fluent = true)
    public static class Builder {

        private File    file;
        private String  name;
        private String  pattern = "%date{yyyy-MM-dd HH:mm:ss.SSS} [%-20class{0}][%-15thread] %-6level %msg%n";
        private boolean useRolling = false;
        private String  fileNamePattern = ".%d{yyyy-MM-dd}";
        private int     maxHistory = 7;
        private Function<String, String> generateFileNamePattern = fileName->fileName+fileNamePattern;

        public FileLog build() throws RuntimeException {
            if(file==null) {
                throw new RuntimeException("file must be set");
            }
            if(name==null) {
                name = file.getName();
            }
            return new FileLog(file, name, pattern, useRolling,
                    fileNamePattern, maxHistory, generateFileNamePattern);
        }
    }

    private void doFileValidity(File file) {
        if(file.exists()) {
            file.delete();
        }
    }

    private Logger createLogger(File file) {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

        FileAppender appender = new FileAppender();
        appender.setContext(context);
        appender.setName(file.getName());
        appender.setFile(file.toString());

        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(context);
        encoder.setPattern("%date{yyyy-MM-dd HH:mm:ss.SSS} [%-20class{0}][%-15thread] %-6level %msg%n");
        encoder.start();
        appender.setEncoder(encoder);

        appender.start();

        Logger log = context.getLogger(name);
        log.addAppender(appender);
        log.setAdditive(false);
        return log;
    }

    private FileAppender getFileAppender(File file) {
        FileAppender appender = new FileAppender();
        appender.setName(file.getName());
        appender.setFile(file.toString());
        return appender;
    }

    private PatternLayoutEncoder getEncoder() {
        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setPattern("%date{yyyy-MM-dd HH:mm:ss.SSS} [%-20class{0}][%-15thread] %-6level %msg%n");
        encoder.start();
        return encoder;
    }

    private void printStamp() {
        final String lineSeparator = "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~";
        if(logger!=null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            String timeString = dateFormat.format(new Date(System.currentTimeMillis()));
            logger.info(lineSeparator);
            logger.info("Logging starts at "+timeString);
            logger.info(lineSeparator);
        }
    }


    public void info(String msg) {
        info(true, msg);
    }

    public void info(boolean fileOnly, String msg) {
        if(logger!=null) {
            logger.info(msg);
        }
        if(fileOnly==false) {
            super.info(msg);
        }
    }

    public void info(String msg, LogType ... types) {
        if(logger!=null) {
            logger.info(msg);
        }
        super.info(msg, types);
    }

    public void error(String msg) {
        error(true, msg);
    }

    public void error(boolean fileOnly, String msg) {
        if(logger!=null) {
            logger.error(msg);
        }
        if(fileOnly==false) {
            super.error(msg);
        }
    }

    public void error(String msg, LogType ... types) {
        if(logger!=null) {
            logger.error(msg);
        }
        super.error(msg, types);
    }

    public void warn(String msg) {
        warn(true, msg);
    }

    public void warn(boolean fileOnly, String msg) {
        if(logger!=null) {
            logger.warn(msg);
        }
        if(fileOnly==false) {
            super.warn(msg);
        }
    }

    public void warn(String msg, LogType ... types) {
        if(logger!=null) {
            logger.warn(msg);
        }
        super.warn(msg, types);
    }

    public void debug(String msg) {
        debug(true, msg);
    }

    public void debug(boolean fileOnly, String msg) {
        if(logger!=null) {
            logger.debug(msg);
        }
        if(fileOnly==false) {
            super.debug(msg);
        }
    }

    public void debug(String msg, LogType ... types) {
        if(logger!=null) {
            logger.debug(msg);
        }
        super.debug(msg, types);
    }

    public void trace(String msg) {
        trace(true, msg);
    }

    public void trace(boolean fileOnly, String msg) {
        if(logger!=null) {
            logger.trace(msg);
        }
        if(fileOnly==false) {
            super.trace(msg);
        }
    }

    public void trace(String msg, LogType ... types) {
        if(logger!=null) {
            logger.trace(msg);
        }
        super.trace(msg, types);
    }
}

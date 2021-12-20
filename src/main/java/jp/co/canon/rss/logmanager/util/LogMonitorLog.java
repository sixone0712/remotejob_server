package jp.co.canon.rss.logmanager.util;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class LogMonitorLog {

    private Logger log;

    @Autowired
    private LogMonitorLogFactory factory;

    public LogMonitorLog(Class clazz) {
        this(clazz.getName());
    }

    public LogMonitorLog(String name) {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        log = context.getLogger(name);
    }

    public void info(String msg) {
        log.info(msg);
    }

    public void info(String msg, LogType ... types) {
        for(LogType type: types) {
            Logger logger = factory.getLogger(type);
            if(logger!=null) {
                logger.info(msg);
            }
        }
        log.info(msg);
    }

    public void error(String msg) {
        log.error(msg);
    }

    public void error(String msg, LogType ... types) {
        for(LogType type: types) {
            Logger logger = factory.getLogger(type);
            if(logger!=null) {
                logger.error(msg);
            }
        }
        log.error(msg);
    }

    public void warn(String msg) {
        log.warn(msg);
    }

    public void warn(String msg, LogType ... types) {
        for(LogType type: types) {
            Logger logger = factory.getLogger(type);
            if(logger!=null) {
                logger.warn(msg);
            }
        }
        log.warn(msg);
    }

    public void debug(String msg) {
        log.debug(msg);
    }

    public void debug(String msg, LogType ... types) {
        for(LogType type: types) {
            Logger logger = factory.getLogger(type);
            if(logger!=null) {
                logger.debug(msg);
            }
        }
        log.debug(msg);
    }

    public void trace(String msg) {
        log.trace(msg);
    }

    public void trace(String msg, LogType ... types) {
        for(LogType type: types) {
            Logger logger = factory.getLogger(type);
            if(logger!=null) {
                logger.trace(msg);
            }
        }
        log.trace(msg);
    }
}

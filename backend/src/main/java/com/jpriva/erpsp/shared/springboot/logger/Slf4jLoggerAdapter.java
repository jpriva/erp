package com.jpriva.erpsp.shared.springboot.logger;

import com.jpriva.erpsp.shared.domain.ports.out.LoggerPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Slf4jLoggerAdapter implements LoggerPort {

    private final Logger log;

    public Slf4jLoggerAdapter(Class<?> clazz) {
        this.log = LoggerFactory.getLogger(clazz);
    }

    @Override
    public void error(String message, Object... args) {
        log.error(message, args);
    }

    @Override
    public void warn(String message, Object... args) {
        log.warn(message, args);
    }

    @Override
    public void info(String message, Object... args) {
        log.info(message, args);
    }

    @Override
    public void debug(String message, Object... args) {
        log.debug(message, args);
    }
}

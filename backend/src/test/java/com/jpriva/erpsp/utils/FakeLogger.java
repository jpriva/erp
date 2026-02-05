package com.jpriva.erpsp.utils;

import com.jpriva.erpsp.shared.domain.ports.out.LoggerPort;
import org.slf4j.helpers.MessageFormatter;

public class FakeLogger implements LoggerPort {

    private void print(String level, String msg, Object... args) {
        String formattedMsg = MessageFormatter.arrayFormat(msg, args).getMessage();
        System.out.println("[" + level + "] " + formattedMsg);
    }

    @Override
    public void info(String msg, Object... args) {
        print("INFO", msg, args);
    }

    @Override
    public void error(String msg, Object... args) {
        String formattedMsg = MessageFormatter.arrayFormat(msg, args).getMessage();
        System.err.println("[ERROR] " + formattedMsg);
    }

    @Override
    public void warn(String msg, Object... args) {
        print("WARN", msg, args);
    }

    @Override
    public void debug(String msg, Object... args) {
        print("DEBUG", msg, args);
    }
}

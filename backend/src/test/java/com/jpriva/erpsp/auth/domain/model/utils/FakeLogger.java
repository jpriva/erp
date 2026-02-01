package com.jpriva.erpsp.auth.domain.model.utils;

import com.jpriva.erpsp.auth.domain.ports.out.LoggerPort;

public class FakeLogger implements LoggerPort {
    @Override
    public void info(String msg, Object... args) {
        System.out.println("[INFO] " + msg);
    }

    @Override
    public void error(String msg, Object... args) {
        System.err.println("[ERROR] " + msg);
    }

    @Override
    public void warn(String msg, Object... args) {
        System.out.println("[WARN] " + msg);
    }

    @Override
    public void debug(String msg, Object... args) {
        System.out.println("[DEBUG] " + msg);
    }
}

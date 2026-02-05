package com.jpriva.erpsp.utils;

import com.jpriva.erpsp.shared.domain.ports.out.TransactionalPort;

import java.util.function.Supplier;

public class FakeTransactional implements TransactionalPort {
    @Override
    public <T> T execute(Supplier<T> action) {
        return action.get();
    }

    @Override
    public <T> T execute(Supplier<T> action, Runnable onError) {
        try {
            return action.get();
        } catch (Exception e) {
            onError.run();
            throw e;
        }
    }

    @Override
    public <T> T executeReadOnly(Supplier<T> action) {
        return action.get();
    }

    @Override
    public <T> T executeReadOnly(Supplier<T> action, Runnable onError) {
        try {
            return action.get();
        } catch (Exception e) {
            onError.run();
            throw e;
        }
    }

    @Override
    public void execute(Runnable action) {
        action.run();
    }

    @Override
    public void execute(Runnable action, Runnable onError) {
        try {
            action.run();
        } catch (Exception e) {
            onError.run();
            throw e;
        }
    }
}

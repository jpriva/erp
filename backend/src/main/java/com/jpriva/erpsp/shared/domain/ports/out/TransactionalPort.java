package com.jpriva.erpsp.shared.domain.ports.out;

import java.util.function.Supplier;

/**
 * Port for transactional operations.
 */
public interface TransactionalPort {

    /**
     * Executes a read/write transaction and return a result.
     *
     * @param action the action to execute
     * @param <T>    the type of the result
     * @return the result of the action
     */
    <T> T execute(Supplier<T> action);

    <T> T execute(Supplier<T> action, Runnable onError);

    /**
     * Executes a read-only transaction and return a result.
     *
     * @param action the action to execute
     * @param <T>    the type of the result
     * @return the result of the action
     */
    <T> T executeReadOnly(Supplier<T> action);

    <T> T executeReadOnly(Supplier<T> action, Runnable onError);

    /**
     * Executes a read/write transaction without returning a result.
     *
     * @param action the action to execute
     */
    void execute(Runnable action);

    void execute(Runnable action, Runnable onError);
}

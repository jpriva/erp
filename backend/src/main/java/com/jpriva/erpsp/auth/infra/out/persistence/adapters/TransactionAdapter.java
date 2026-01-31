package com.jpriva.erpsp.auth.infra.out.persistence.adapters;

import com.jpriva.erpsp.auth.domain.ports.out.TransactionalPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.function.Supplier;

@Component
public class TransactionAdapter implements TransactionalPort {
    private final TransactionTemplate writeTemplate;
    private final TransactionTemplate readOnlyTemplate;

    public TransactionAdapter(PlatformTransactionManager transactionManager) {
        this.writeTemplate = new TransactionTemplate(transactionManager);
        this.readOnlyTemplate = new TransactionTemplate(transactionManager);
        this.readOnlyTemplate.setReadOnly(true);
    }

    @Override
    public <T> T execute(Supplier<T> action, Runnable onError) {
        return decorateWithOnError(writeTemplate, action, onError);
    }

    @Override
    public <T> T execute(Supplier<T> action) {
        return writeTemplate.execute(_ -> action.get());
    }

    @Override
    public <T> T executeReadOnly(Supplier<T> action, Runnable onError) {
        return decorateWithOnError(readOnlyTemplate, action, onError);
    }

    @Override
    public <T> T executeReadOnly(Supplier<T> action) {
        return readOnlyTemplate.execute(_ -> action.get());
    }

    @Override
    public void execute(Runnable action, Runnable onError) {
        decorateWithOnError(writeTemplate, () -> {
            action.run();
            return null;
        }, onError);
    }

    @Override
    public void execute(Runnable action) {
        writeTemplate.execute(_ -> {
            action.run();
            return null;
        });
    }

    private <T> T decorateWithOnError(TransactionTemplate template, Supplier<T> action, Runnable onError) {
        return template.execute(_ -> {
            try {
                return action.get();
            } catch (Exception e) {
                if (onError != null) {
                    registerRollbackSynchronization(onError);
                }
                throw e;
            }
        });
    }

    private void registerRollbackSynchronization(Runnable onError) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCompletion(int status) {
                            if (status == STATUS_ROLLED_BACK) {
                                onError.run();
                            }
                        }
                    }
            );
        }
    }
}

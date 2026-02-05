package com.jpriva.erpsp.notification.domain.port.out;

import com.jpriva.erpsp.notification.domain.model.Notification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificationRepositoryPort {
    Notification save(Notification notification);

    Optional<Notification> findById(UUID id);

    List<Notification> findByNotSent();
}

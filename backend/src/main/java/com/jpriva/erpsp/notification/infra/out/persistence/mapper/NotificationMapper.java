package com.jpriva.erpsp.notification.infra.out.persistence.mapper;

import com.jpriva.erpsp.notification.domain.model.Notification;
import com.jpriva.erpsp.notification.infra.out.persistence.entity.NotificationEntity;

public class NotificationMapper {
    private NotificationMapper() {
    }

    public static NotificationEntity toEntity(Notification notification) {
        return NotificationEntity.builder()
                .notificationId(notification.getId())
                .userId(notification.getUserId().value())
                .recipient(notification.getRecipient())
                .subject(notification.getSubject())
                .content(notification.getContent())
                .channel(notification.getChannel())
                .status(notification.getStatus())
                .retryCount(notification.getRetryCount())
                .errorLog(notification.getErrorLog())
                .createdAt(notification.getCreatedAt())
                .sentAt(notification.getSentAt())
                .isRead(notification.isRead())
                .build();
    }

    public static Notification toDomain(NotificationEntity entity) {
        return Notification.fromDatabase(
                entity.getNotificationId(),
                entity.getUserId(),
                entity.getRecipient(),
                entity.getSubject(),
                entity.getContent(),
                entity.getChannel(),
                entity.getStatus(),
                entity.getRetryCount(),
                entity.getErrorLog(),
                entity.getCreatedAt(),
                entity.getSentAt(),
                entity.isRead()
        );
    }
}

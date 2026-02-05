package com.jpriva.erpsp.notification.infra.out.persistence.entity;

import com.jpriva.erpsp.notification.domain.model.NotificationChannel;
import com.jpriva.erpsp.notification.domain.model.NotificationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(schema = "notification", name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationEntity {
    @Id
    @Column(name = "notification_id", columnDefinition = "UUID")
    private UUID notificationId;

    @Column(name = "user_id", nullable = false, updatable = false, columnDefinition = "UUID")
    private UUID userId;

    @Column(name = "recipient", nullable = false, updatable = false)
    private String recipient;

    @Column(name = "subject", nullable = false, updatable = false)
    private String subject;

    @Column(name = "content", nullable = false, updatable = false)
    private String content;

    @Column(name = "channel", nullable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private NotificationChannel channel;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationStatus status;

    @Column(name = "retry_count", nullable = false)
    private int retryCount;

    @Column(name = "error_log")
    private String errorLog;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "sent_at")
    private Instant sentAt;

    @Column(name = "is_read", nullable = false)
    private boolean isRead;
}

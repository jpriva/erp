package com.jpriva.erpsp.notification.domain.model;

import com.jpriva.erpsp.shared.domain.exceptions.ErpPersistenceCompromisedException;
import com.jpriva.erpsp.shared.domain.model.UserId;

import java.time.Instant;
import java.util.UUID;

public class Notification {
    private final UUID id;
    private final UserId userId;
    private final String recipient;
    private final String subject;
    private final String content;
    private final NotificationChannel channel;
    private NotificationStatus status;
    private int retryCount;
    private String errorLog;
    private final Instant createdAt;
    private Instant sentAt;
    private boolean isRead;

    private Notification(
            UUID id, UserId userId, String recipient, String subject, String content, NotificationChannel channel,
            NotificationStatus status, int retryCount, String errorLog, Instant createdAt, Instant sentAt,
            boolean isRead
    ) {
        this.id = id;
        this.userId = userId;
        this.recipient = recipient;
        this.subject = subject;
        this.content = content;
        this.channel = channel;
        this.status = status;
        this.retryCount = retryCount;
        this.errorLog = errorLog;
        this.createdAt = createdAt;
        this.sentAt = sentAt;
        this.isRead = isRead;
    }

    public static Notification createEmail(
            UserId userId,
            String recipient,
            String subject,
            String content
    ) {
        return new Notification(
                UUID.randomUUID(),
                userId,
                recipient,
                subject,
                content,
                NotificationChannel.EMAIL,
                NotificationStatus.PENDING,
                0,
                null,
                Instant.now(),
                null,
                false
        );
    }

    public static Notification fromDatabase(
            UUID id, UUID userId, String recipient, String subject, String content, NotificationChannel channel,
            NotificationStatus status, int retryCount, String errorLog, Instant createdAt, Instant sentAt,
            boolean isRead
    ) {
        try {
            return new Notification(id, new UserId(userId), recipient, subject, content, channel, status,
                    retryCount, errorLog, createdAt, sentAt, isRead);
        } catch (Exception e) {
            throw new ErpPersistenceCompromisedException("NOTIFICATION", e);
        }
    }

    public void markAsSent() {
        this.status = NotificationStatus.SENT;
        this.sentAt = Instant.now();
    }

    public void markAsFailed(String error) {
        this.status = NotificationStatus.FAILED;
        this.errorLog = error;
        this.retryCount++;
    }

    public void markAsRead() {
        this.isRead = true;
    }

    public UUID getId() {
        return id;
    }

    public UserId getUserId() {
        return userId;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getSubject() {
        return subject;
    }

    public String getContent() {
        return content;
    }

    public NotificationChannel getChannel() {
        return channel;
    }

    public NotificationStatus getStatus() {
        return status;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public String getErrorLog() {
        return errorLog;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getSentAt() {
        return sentAt;
    }

    public boolean isRead() {
        return isRead;
    }
}

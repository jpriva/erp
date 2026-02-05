CREATE SCHEMA IF NOT EXISTS notification;

CREATE TYPE notification.notification_status AS ENUM (
    'PENDING',
    'SENT',
    'FAILED'
    );

CREATE TYPE notification.notification_channel AS ENUM (
    'EMAIL'
    );

CREATE TABLE notification.notifications
(
    notification_id UUID PRIMARY KEY,
    user_id         UUID                              NOT NULL,
    recipient       VARCHAR(255)                      NOT NULL,
    subject         VARCHAR(100),
    content         TEXT                              NOT NULL,
    channel         notification.notification_channel NOT NULL,
    status          notification.notification_status  NOT NULL,
    retry_count     INTEGER                           NOT NULL DEFAULT 0,
    error_log       TEXT,
    created_at      TIMESTAMP                         NOT NULL DEFAULT CURRENT_TIMESTAMP,
    sent_at         TIMESTAMP,
    is_read         BOOLEAN                           NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_notifications_user_id ON notification.notifications (user_id);
CREATE INDEX idx_notifications_status ON notification.notifications (status);
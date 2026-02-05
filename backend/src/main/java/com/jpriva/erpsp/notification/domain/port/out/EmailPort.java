package com.jpriva.erpsp.notification.domain.port.out;

public interface EmailPort {
    void sendEmail(String recipient, String subject, String body);
}

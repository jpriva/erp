package com.jpriva.erpsp.notification.domain.port.out;

import com.jpriva.erpsp.shared.domain.events.VerifyUserEmail;

public interface AuthTemplatePort {
    String generateVerificationEmail(VerifyUserEmail payload);
}

package com.jpriva.erpsp.auth.domain.ports.out;

import com.jpriva.erpsp.shared.domain.events.VerifyUserEmail;

public interface AuthEventPort {
    void publishRegisterUserEvent(VerifyUserEmail event);
}

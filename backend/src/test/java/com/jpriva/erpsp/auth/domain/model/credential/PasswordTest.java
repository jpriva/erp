package com.jpriva.erpsp.auth.domain.model.credential;

import com.jpriva.erpsp.auth.domain.model.utils.PasswordTestUtils;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class PasswordTest {

    @Test
    void constructor_Success(){
        Password password = Password.create("password", PasswordTestUtils.fakeHasher);
        assertThat(password).isNotNull();
    }
}

package com.jpriva.erpsp.auth.domain.model.user;

import com.jpriva.erpsp.auth.domain.constants.AuthErrorCode;
import com.jpriva.erpsp.auth.domain.constants.UserValidationError;
import com.jpriva.erpsp.shared.domain.exceptions.ErpPersistenceCompromisedException;
import com.jpriva.erpsp.shared.domain.exceptions.ErpValidationException;
import com.jpriva.erpsp.shared.domain.model.Email;
import com.jpriva.erpsp.shared.domain.model.PersonName;
import com.jpriva.erpsp.shared.domain.model.ValidationError;
import com.jpriva.erpsp.shared.domain.utils.ValidationErrorUtils;

import java.time.Instant;
import java.util.UUID;

public class User {
    private final UserId userId;
    private Email email;
    private PersonName name;
    private UserStatus status;
    private Instant createdAt;

    public User(UserId userId, Email email, PersonName name, UserStatus status, Instant createdAt) {
        var val = new ValidationError.Builder();
        if (userId == null) {
            val.addError(UserValidationError.ID_EMPTY);
        }
        if (email == null) {
            val.addError(UserValidationError.EMAIL_EMPTY);
        }
        if (name == null) {
            val.addError(UserValidationError.NAME_EMPTY);
        }
        if (status == null) {
            val.addError(UserValidationError.STATUS_EMPTY);
        }
        if (createdAt == null) {
            val.addError(UserValidationError.CREATED_AT_EMPTY);
        }
        ValidationErrorUtils.validate(AuthErrorCode.AUTH_MODULE, val);
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.status = status;

    }

    public static User create(String emailStr, String firstName, String lastName) {
        var val = new ValidationError.Builder();
        UserId userId = UserId.generate();
        Email email = null;
        PersonName name = null;
        try {
            email = new Email(emailStr);
        } catch (ErpValidationException ex) {
            val.addValidation(ex.getValidationErrors());
        }
        try {
            name = new PersonName(firstName, lastName);
        } catch (ErpValidationException ex) {
            val.addValidation(ex.getValidationErrors());
        }

        ValidationErrorUtils.validate(AuthErrorCode.AUTH_MODULE, val);
        return new User(userId, email, name, UserStatus.EMAIL_NOT_VERIFIED, Instant.now());
    }

    public static User fromPersistence(UUID uuid, String mail, String firstName, String lastName, String status, Instant createdAt) {
        User user;
        try {
            UserStatus userStatus = UserStatus.of(status);
            user = new User(new UserId(uuid), new Email(mail), new PersonName(firstName, lastName), userStatus, createdAt);
        } catch (ErpValidationException ex) {
            throw new ErpPersistenceCompromisedException(AuthErrorCode.AUTH_MODULE, ex);
        }
        return user;
    }

    public void changeData(String emailStr, String firstName, String lastName) {
        var val = new ValidationError.Builder();
        try {
            changeEmail(emailStr);
        } catch (ErpValidationException ex) {
            val.addValidation(ex.getValidationErrors());
        }
        try {
            changeFirstName(firstName);
        } catch (ErpValidationException ex) {
            val.addValidation(ex.getValidationErrors());
        }
        try {
            changeLastName(lastName);
        } catch (ErpValidationException ex) {
            val.addValidation(ex.getValidationErrors());
        }
        ValidationErrorUtils.validate(AuthErrorCode.AUTH_MODULE, val);
    }

    public void changeEmail(String emailStr) {
        this.email = new Email(emailStr);
    }

    public void changeFirstName(String firstName) {
        this.name = new PersonName(firstName, this.name.lastName());
    }

    public void changeLastName(String lastName) {
        this.name = new PersonName(this.name.firstName(), lastName);
    }

    public void changeStatus(UserStatus status) {
        this.status = status;
    }

    public UserId getUserId() {
        return userId;
    }

    public Email getEmail() {
        return email;
    }

    public PersonName getName() {
        return name;
    }

    public UserStatus getStatus() {
        return status;
    }

}

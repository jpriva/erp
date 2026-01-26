package com.jpriva.erpsp.auth.domain.model.user;

import com.jpriva.erpsp.auth.domain.constants.AuthErrorCode;
import com.jpriva.erpsp.shared.domain.exceptions.ErpPersistenceCompromisedException;
import com.jpriva.erpsp.shared.domain.exceptions.ErpValidationException;
import com.jpriva.erpsp.shared.domain.model.Email;
import com.jpriva.erpsp.shared.domain.model.PersonName;
import com.jpriva.erpsp.shared.domain.model.ValidationError;
import com.jpriva.erpsp.shared.domain.utils.ValidationErrorUtils;

import java.util.UUID;

public class User {
    private static final String USER_ID_NULL_ERROR = "User ID can't be empty";
    private static final String EMAIL_NULL_ERROR = "Email can't be empty";
    private static final String NAME_NULL_ERROR = "Name can't be empty";
    private static final String STATUS_NULL_ERROR = "Status can't be empty";
    private static final String FIELD_USER_ID = "userId";
    private static final String FIELD_EMAIL = "email";
    private static final String FIELD_NAME = "name";
    private static final String FIELD_STATUS = "status";
    private final UserId userId;
    private Email email;
    private PersonName name;
    private UserStatus status;

    public User(UserId userId, Email email, PersonName name, UserStatus status) {
        var val = new ValidationError.Builder();
        if (userId == null) {
            val.addError(FIELD_USER_ID, USER_ID_NULL_ERROR);
        }
        if (email == null) {
            val.addError(FIELD_EMAIL, EMAIL_NULL_ERROR);
        }
        if (name == null) {
            val.addError(FIELD_NAME, NAME_NULL_ERROR);
        }
        if (status == null) {
            val.addError(FIELD_STATUS, STATUS_NULL_ERROR);
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
        return new User(userId, email, name, UserStatus.EMAIL_NOT_VERIFIED);
    }

    public static User fromPersistence(UUID uuid, String mail, String firstName, String lastName, String status) {
        User user;
        try {
            UserStatus userStatus = UserStatus.of(status);
            user = new User(new UserId(uuid), new Email(mail), new PersonName(firstName, lastName), userStatus);
        } catch (ErpValidationException ex) {
            throw new ErpPersistenceCompromisedException(AuthErrorCode.AUTH_MODULE, ex);
        }
        return user;
    }

    public void changeData(String emailStr, String firstName, String lastName) {
        changeEmail(emailStr);
        changeFirstName(firstName);
        changeLastName(lastName);
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

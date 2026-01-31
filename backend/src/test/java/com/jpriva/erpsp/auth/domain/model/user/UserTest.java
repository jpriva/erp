package com.jpriva.erpsp.auth.domain.model.user;

import com.jpriva.erpsp.shared.domain.exceptions.ErpPersistenceCompromisedException;
import com.jpriva.erpsp.shared.domain.exceptions.ErpValidationException;
import com.jpriva.erpsp.shared.domain.model.Email;
import com.jpriva.erpsp.shared.domain.model.PersonName;
import com.jpriva.erpsp.shared.domain.utils.ErpExceptionTestUtils;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static com.jpriva.erpsp.shared.domain.utils.ValidationErrorAssertions.assertHasFieldError;

class UserTest {

    @Nested
    class ConstructorTests {
        @Test
        void constructor_Success() {
            UUID uuid = UUID.randomUUID();
            Instant createdAt = Instant.now();
            User user = new User(new UserId(uuid), new Email("test@example.com"), new PersonName("John", "Doe"), UserStatus.ACTIVE, createdAt);
            assertThat(user).isNotNull();
            assertThat(user.getUserId()).isNotNull();
            assertThat(user.getEmail()).isNotNull();
            assertThat(user.getName()).isNotNull();
            assertThat(user.getUserId().value()).isEqualTo(uuid);
            assertThat(user.getEmail().value()).isEqualTo("test@example.com");
            assertThat(user.getName().firstName()).isEqualTo("John");
            assertThat(user.getName().lastName()).isEqualTo("Doe");
        }

        @Test
        void constructor_ShouldFailForUserIdNull() {
            assertThatThrownBy(() -> new User(null, new Email("test@example.com"), new PersonName("John", "Doe"), UserStatus.ACTIVE, Instant.now()))
                    .isInstanceOf(ErpValidationException.class)
                    .satisfies(exception -> {
                        ErpValidationException ex = (ErpValidationException) exception;
                        ErpExceptionTestUtils.printExceptionDetails(ex);
                        assertHasFieldError(ex, "userId");
                    });
        }

        @Test
        void constructor_ShouldFailForEmailNull() {
            assertThatThrownBy(() -> new User(UserId.generate(), null, new PersonName("John", "Doe"), UserStatus.ACTIVE, Instant.now()))
                    .isInstanceOf(ErpValidationException.class)
                    .satisfies(exception -> {
                        ErpValidationException ex = (ErpValidationException) exception;
                        ErpExceptionTestUtils.printExceptionDetails(ex);
                        assertHasFieldError(ex, "email");
                    });
        }

        @Test
        void constructor_ShouldFailForNameNull() {
            assertThatThrownBy(() -> new User(UserId.generate(), new Email("test@example.com"), null, UserStatus.ACTIVE, Instant.now()))
                    .isInstanceOf(ErpValidationException.class)
                    .satisfies(exception -> {
                        ErpValidationException ex = (ErpValidationException) exception;
                        ErpExceptionTestUtils.printExceptionDetails(ex);
                        assertHasFieldError(ex, "name");
                    });
        }
    }

    @Nested
    class CreateTests {

        @Test
        void create_Success() {
            User user = User.create("test@example.com", "John", "Doe");
            assertThat(user).isNotNull();
            assertThat(user.getUserId()).isNotNull();
            assertThat(user.getEmail()).isNotNull();
            assertThat(user.getName()).isNotNull();
            assertThat(user.getEmail().value()).isEqualTo("test@example.com");
            assertThat(user.getName().firstName()).isEqualTo("John");
            assertThat(user.getName().lastName()).isEqualTo("Doe");
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"a","a@a"})
        void create_ShouldPropagateErrorsFromEmailError(String email) {
            assertThatThrownBy(()->User.create(email, "John", "Doe"))
                    .isInstanceOf(ErpValidationException.class)
                    .satisfies(exception -> {
                        ErpValidationException ex = (ErpValidationException) exception;
                        ErpExceptionTestUtils.printExceptionDetails(ex);
                        assertHasFieldError(ex, "email");
                    });
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"a"})
        void create_ShouldPropagateErrorsFromNameError(String name) {
            assertThatThrownBy(()->User.create("test@example.com", name, "Doe"))
                    .isInstanceOf(ErpValidationException.class)
                    .satisfies(exception -> {
                        ErpValidationException ex = (ErpValidationException) exception;
                        ErpExceptionTestUtils.printExceptionDetails(ex);
                        assertHasFieldError(ex, "firstName");
                    });
        }
    }

    @Nested
    class FromPersistenceTests {

        @Test
        void fromPersistence_Success() {
            UUID uuid = UUID.randomUUID();
            Instant createdAt = Instant.now();
            User user = User.fromPersistence(uuid, "test@example.com", "John", "Doe", "ACTIVE", createdAt);
            assertThat(user).isNotNull();
            assertThat(user.getUserId()).isNotNull();
            assertThat(user.getEmail()).isNotNull();
            assertThat(user.getName()).isNotNull();
            assertThat(user.getUserId().value()).isEqualTo(uuid);
            assertThat(user.getEmail().value()).isEqualTo("test@example.com");
            assertThat(user.getName().firstName()).isEqualTo("John");
            assertThat(user.getName().lastName()).isEqualTo("Doe");
            assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
        }

        @Test
        void fromPersistence_ShouldThrowPersistenceCompromisedExceptionIfDoesntMatchDomain() {
            assertThatThrownBy(() -> User.fromPersistence(UUID.randomUUID(), null, "John", "Doe", "ACTIVE", Instant.now()))
                    .isInstanceOf(ErpPersistenceCompromisedException.class)
                    .satisfies(exception -> {
                        ErpPersistenceCompromisedException ex = (ErpPersistenceCompromisedException) exception;
                        ErpExceptionTestUtils.printExceptionDetails(ex);
                    });
        }
    }

    @Nested
    class ChangeTests {
        @Test
        void changeEmail_Success() {
            User user = User.create("test@example.com", "John", "Doe");
            user.changeEmail("test2@example.com");
            assertThat(user.getEmail().value()).isEqualTo("test2@example.com");
        }

        @Test
        void changeFirstName_Success() {
            User user = User.create("test@example.com", "John", "Doe");
            user.changeFirstName("Andrew");
            assertThat(user.getName().firstName()).isEqualTo("Andrew");
        }

        @Test
        void changeLastName_Success() {
            User user = User.create("test@example.com", "John", "Doe");
            user.changeLastName("Smith");
            assertThat(user.getName().lastName()).isEqualTo("Smith");
        }

        @Test
        void changeStatus_Success() {
            User user = User.create("test@example.com", "John", "Doe");
            user.changeStatus(UserStatus.BLOCKED);
            assertThat(user.getStatus()).isEqualTo(UserStatus.BLOCKED);
        }

        @Test
        void changeData_Success() {
            User user = User.create("test@example.com", "John", "Doe");
            user.changeData("test2@example.com", "Andrew", "Smith");
            assertThat(user.getEmail().value()).isEqualTo("test2@example.com");
            assertThat(user.getName().firstName()).isEqualTo("Andrew");
            assertThat(user.getName().lastName()).isEqualTo("Smith");
        }
    }
}

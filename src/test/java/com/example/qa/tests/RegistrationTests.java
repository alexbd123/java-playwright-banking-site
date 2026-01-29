package com.example.qa.tests;

import com.example.qa.enums.RegistrationFieldErrorMessage;
import com.example.qa.models.User;
import com.example.qa.models.UserFactory;
import com.example.qa.pages.RegistrationPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class RegistrationTests extends BaseTest {

    private RegistrationPage registrationPage;
    private User user;

    @BeforeEach
    void openRegistration() {
        registrationPage = new RegistrationPage(page);
        user = UserFactory.validRandomUser();
    }

    @Test
    void userCanNavigateToRegistrationPage() {
        assertThat(registrationPage.signupMessage()).isVisible();
        assertThat(registrationPage.submitRegistrationButton()).isVisible();
    }

    @Test
    void validUserCanSubmitRegistration() {
        registrationPage.fillRegistrationFieldsWithValidUser(UserFactory.validRandomUser());
        registrationPage.clickSubmitRegistrationButton();
        assertThat(registrationPage.registrationSuccessfulMessage()).isVisible();
    }

    @ParameterizedTest(name = "User cannot register without filling in {0} field")
    @EnumSource(value = RegistrationFieldErrorMessage.class, mode = EnumSource.Mode.EXCLUDE, names = "PASSWORD_MISMATCH")
    void userCannotRegisterWithFieldLeftBlank(RegistrationFieldErrorMessage errorValidationCase) {
        registrationPage.fillRegistrationFieldsWithValidUserAndClearField(
                user,
                errorValidationCase
        );
        registrationPage.clickSubmitRegistrationButton();
        registrationPage.assertErrorOnRegistrationPage(
                registrationPage.getErrorMessageLocator(errorValidationCase), errorValidationCase.errorMessage
        );
    }

    @Test
    void userCanSubmitRegistrationWithoutPhoneNumber() {
        registrationPage.fillRegistrationFieldsWithValidUser(user);
        registrationPage.clearPhoneNumber();
        registrationPage.clickSubmitRegistrationButton();
        assertThat(registrationPage.registrationSuccessfulMessage()).isVisible();
    }

    @Test
    void userCannotRegisterIfPasswordsDoNotMatch() {
        String confirmPasswordNotIdentical = user.getPassword() + "abc";
        registrationPage.fillRegistrationFieldsWithValidUser(user);
        registrationPage.fillConfirmPassword(confirmPasswordNotIdentical);
        registrationPage.clickSubmitRegistrationButton();
        var passwordNotIdenticalErrorMessage = registrationPage.noConfirmOrNoMatchPasswordErrorMessage();
        assertThat(passwordNotIdenticalErrorMessage).isVisible();
        assertThat(passwordNotIdenticalErrorMessage).hasText(RegistrationFieldErrorMessage.PASSWORD_MISMATCH.errorMessage);
    }
}
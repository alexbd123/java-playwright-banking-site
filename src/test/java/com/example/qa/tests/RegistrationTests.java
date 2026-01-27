package com.example.qa.tests;

import com.example.qa.enums.RegistrationField;
import com.example.qa.models.User;
import com.example.qa.models.UserFactory;
import com.example.qa.pages.NavigationPage;
import com.example.qa.pages.RegistrationPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class RegistrationTests extends BaseTest {

    private RegistrationPage registrationPage;
    private NavigationPage goTo;

    @BeforeEach
    void openRegistration() {
        goTo = new NavigationPage(page);
        goTo.registrationPage();
        registrationPage = new RegistrationPage(page);
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

    @Test
    void userCanSubmitRegistrationWithoutPhoneNumber() {
        registrationPage.fillRegistrationFieldsWithValidUser(UserFactory.validRandomUser());
        registrationPage.fillPhoneNumber("");
        registrationPage.clickSubmitRegistrationButton();
        assertThat(registrationPage.registrationSuccessfulMessage()).isVisible();
    }

    @Test
    void userCannotRegisterWithoutFirstName() {
        registrationPage.fillRegistrationFieldsWithValidUserAndClearField(
                UserFactory.validRandomUser(),
                RegistrationField.FIRST_NAME
        );
        registrationPage.clickSubmitRegistrationButton();
        registrationPage.assertErrorOnRegistrationPage(
            registrationPage.noFirstNameErrorMessage(), "First name is required.");
    }

    @Test
    void userCannotRegisterWithoutLastName() {
        registrationPage.fillRegistrationFieldsWithValidUserAndClearField(
                UserFactory.validRandomUser(),
                RegistrationField.LAST_NAME
        );
        registrationPage.clickSubmitRegistrationButton();
        registrationPage.assertErrorOnRegistrationPage(
            registrationPage.noLastNameErrorMessage(), "Last name is required.");
    }

    @Test
    void userCannotRegisterWithoutAddress() {
        registrationPage.fillRegistrationFieldsWithValidUserAndClearField(
                UserFactory.validRandomUser(),
                RegistrationField.ADDRESS
        );
        registrationPage.clickSubmitRegistrationButton();
        registrationPage.assertErrorOnRegistrationPage(
            registrationPage.noAddressErrorMessage(), "Address is required.");
    }

    @Test
    void userCannotRegisterWithoutCity() {
        registrationPage.fillRegistrationFieldsWithValidUserAndClearField(
                UserFactory.validRandomUser(),
                RegistrationField.CITY
        );
        registrationPage.clickSubmitRegistrationButton();
        registrationPage.assertErrorOnRegistrationPage(
            registrationPage.noCityErrorMessage(), "City is required.");
    }

    @Test
    void userCannotRegisterWithoutState() {
        registrationPage.fillRegistrationFieldsWithValidUserAndClearField(
                UserFactory.validRandomUser(),
                RegistrationField.STATE
        );
        registrationPage.clickSubmitRegistrationButton();
        var noStateErrorMessage = registrationPage.noStateErrorMessage();
        registrationPage.assertErrorOnRegistrationPage(
            noStateErrorMessage, "State is required.");
    }

    @Test
    void userCannotRegisterWithoutZipCode() {
        registrationPage.fillRegistrationFieldsWithValidUserAndClearField(
                UserFactory.validRandomUser(),
                RegistrationField.ZIP_CODE
        );
        registrationPage.clickSubmitRegistrationButton();
        registrationPage.assertErrorOnRegistrationPage(
            registrationPage.noZipCodeErrorMessage(), "Zip Code is required.");
    }

    @Test
    void userCannotRegisterWithoutSsn() {
        registrationPage.fillRegistrationFieldsWithValidUserAndClearField(
                UserFactory.validRandomUser(),
                RegistrationField.SSN
        );
        registrationPage.clickSubmitRegistrationButton();
        registrationPage.assertErrorOnRegistrationPage(
            registrationPage.noSsnErrorMessage(), "Social Security Number is required.");
    }

    @Test
    void userCannotRegisterWithoutUsername() {
        registrationPage.fillRegistrationFieldsWithValidUserAndClearField(
                UserFactory.validRandomUser(),
                RegistrationField.USERNAME
        );
        registrationPage.clickSubmitRegistrationButton();
        registrationPage.assertErrorOnRegistrationPage(
            registrationPage.noUsernameErrorMessage(), "Username is required.");
    }

    @Test
    void userCannotRegisterWithoutPassword() {
        registrationPage.fillRegistrationFieldsWithValidUserAndClearField(
                UserFactory.validRandomUser(),
                RegistrationField.PASSWORD
        );
        registrationPage.clickSubmitRegistrationButton();
        registrationPage.assertErrorOnRegistrationPage(
            registrationPage.noPasswordErrorMessage(), "Password is required.");
    }

    @Test
    void userCannotRegisterWithoutConfirmingPassword() {
        registrationPage.fillRegistrationFieldsWithValidUserAndClearField(
                UserFactory.validRandomUser(),
                RegistrationField.CONFIRM_PASSWORD
        );
        registrationPage.clickSubmitRegistrationButton();
        registrationPage.assertErrorOnRegistrationPage(
            registrationPage.noConfirmOrNoMatchPasswordErrorMessage(), "Password confirmation is required.");
    }

    @Test
    void userCannotRegisterIfPasswordsDoNotMatch() {
        User user = UserFactory.validRandomUser();
        String confirmPasswordNotIdentical = user.getPassword() + "abc";
        registrationPage.fillRegistrationFieldsWithValidUser(user);
        registrationPage.fillConfirmPassword(confirmPasswordNotIdentical);
        registrationPage.clickSubmitRegistrationButton();
        var noConfirmOrNoMatchPasswordErrorMessage = registrationPage.noConfirmOrNoMatchPasswordErrorMessage();
        assertThat(noConfirmOrNoMatchPasswordErrorMessage).isVisible();
        assertThat(noConfirmOrNoMatchPasswordErrorMessage).hasText("Passwords did not match.");
        assertThat(page).hasURL(NavigationPage.REGISTRATION_PAGE_URL);
    }
}
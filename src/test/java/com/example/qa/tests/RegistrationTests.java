package com.example.qa.tests;

import com.example.qa.models.User;
import com.example.qa.models.UserFactory;
import com.example.qa.pages.NavigationPage;
import com.example.qa.pages.RegistrationPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class RegistrationTests extends BaseTest {

    public RegistrationPage registrationPage;
    public NavigationPage goTo;

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
        registrationPage.fillRegistrationFieldsWithValidUser(UserFactory.validRandomUser());
        registrationPage.fillFirstName("");
        registrationPage.clickSubmitRegistrationButton();
        var noFirstNameErrorMessage = registrationPage.noFirstNameErrorMessage();
        assertThat(noFirstNameErrorMessage).isVisible();
        assertThat(noFirstNameErrorMessage).hasText("First name is required.");
        assertThat(page).hasURL(NavigationPage.REGISTRATION_PAGE_URL);
    }

    @Test
    void userCannotRegisterWithoutLastName() {
        registrationPage.fillRegistrationFieldsWithValidUser(UserFactory.validRandomUser());
        registrationPage.fillLastName("");
        registrationPage.clickSubmitRegistrationButton();
        var noLastNameErrorMessage = registrationPage.noLastNameErrorMessage();
        assertThat(noLastNameErrorMessage).isVisible();
        assertThat(noLastNameErrorMessage).hasText("Last name is required.");
        assertThat(page).hasURL(NavigationPage.REGISTRATION_PAGE_URL);
    }

    @Test
    void userCannotRegisterWithoutAddress() {
        registrationPage.fillRegistrationFieldsWithValidUser(UserFactory.validRandomUser());
        registrationPage.fillAddress("");
        registrationPage.clickSubmitRegistrationButton();
        var noAddressErrorMessage = registrationPage.noAddressErrorMessage();
        assertThat(noAddressErrorMessage).isVisible();
        assertThat(noAddressErrorMessage).hasText("Address is required.");
        assertThat(page).hasURL(NavigationPage.REGISTRATION_PAGE_URL);
    }

    @Test
    void userCannotRegisterWithoutCity() {
        registrationPage.fillRegistrationFieldsWithValidUser(UserFactory.validRandomUser());
        registrationPage.fillCity("");
        registrationPage.clickSubmitRegistrationButton();
        var noCityErrorMessage = registrationPage.noCityErrorMessage();
        assertThat(noCityErrorMessage).isVisible();
        assertThat(noCityErrorMessage).hasText("City is required.");
        assertThat(page).hasURL(NavigationPage.REGISTRATION_PAGE_URL);
    }

    @Test
    void userCannotRegisterWithoutState() {
        registrationPage.fillRegistrationFieldsWithValidUser(UserFactory.validRandomUser());
        registrationPage.fillState("");
        registrationPage.clickSubmitRegistrationButton();
        var noStateErrorMessage = registrationPage.noStateErrorMessage();
        assertThat(noStateErrorMessage).isVisible();
        assertThat(noStateErrorMessage).hasText("State is required.");
        assertThat(page).hasURL(NavigationPage.REGISTRATION_PAGE_URL);
    }

    @Test
    void userCannotRegisterWithoutZipCode() {
        registrationPage.fillRegistrationFieldsWithValidUser(UserFactory.validRandomUser());
        registrationPage.fillZipCode("");
        registrationPage.clickSubmitRegistrationButton();
        var noZipCodeErrorMessage = registrationPage.noZipCodeErrorMessage();
        assertThat(noZipCodeErrorMessage).isVisible();
        assertThat(noZipCodeErrorMessage).hasText("Zip Code is required.");
        assertThat(page).hasURL(NavigationPage.REGISTRATION_PAGE_URL);
    }

    @Test
    void userCannotRegisterWithoutSsn() {
        registrationPage.fillRegistrationFieldsWithValidUser(UserFactory.validRandomUser());
        registrationPage.fillSsn("");
        registrationPage.clickSubmitRegistrationButton();
        var noSsnErrorMessage = registrationPage.noSsnErrorMessage();
        assertThat(noSsnErrorMessage).isVisible();
        assertThat(noSsnErrorMessage).hasText("Social Security Number is required.");
        assertThat(page).hasURL(NavigationPage.REGISTRATION_PAGE_URL);
    }

    @Test
    void userCannotRegisterWithoutUsername() {
        registrationPage.fillRegistrationFieldsWithValidUser(UserFactory.validRandomUser());
        registrationPage.fillUsername("");
        registrationPage.clickSubmitRegistrationButton();
        var noUsernameErrorMessage = registrationPage.noUsernameErrorMessage();
        assertThat(noUsernameErrorMessage).isVisible();
        assertThat(noUsernameErrorMessage).hasText("Username is required.");
        assertThat(page).hasURL(NavigationPage.REGISTRATION_PAGE_URL);
    }

    @Test
    void userCannotRegisterWithoutPassword() {
        registrationPage.fillRegistrationFieldsWithValidUser(UserFactory.validRandomUser());
        registrationPage.fillPassword("");
        registrationPage.clickSubmitRegistrationButton();
        var noPasswordErrorMessage = registrationPage.noPasswordErrorMessage();
        assertThat(noPasswordErrorMessage).isVisible();
        assertThat(noPasswordErrorMessage).hasText("Password is required.");
        assertThat(page).hasURL(NavigationPage.REGISTRATION_PAGE_URL);
    }

    @Test
    void userCannotRegisterWithoutConfirmingPassword() {
        registrationPage.fillRegistrationFieldsWithValidUser(UserFactory.validRandomUser());
        registrationPage.fillConfirmPassword("");
        registrationPage.clickSubmitRegistrationButton();
        var noConfirmOrNoMatchPasswordErrorMessage = registrationPage.noConfirmOrNoMatchPasswordErrorMessage();
        assertThat(noConfirmOrNoMatchPasswordErrorMessage).isVisible();
        assertThat(noConfirmOrNoMatchPasswordErrorMessage).hasText("Password confirmation is required.");
        assertThat(page).hasURL(NavigationPage.REGISTRATION_PAGE_URL);
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
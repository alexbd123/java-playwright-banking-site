package com.example.qa.pages;

import com.example.qa.enums.RegistrationField;
import com.example.qa.models.User;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Locator;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class RegistrationPage {

    private final Page page;
    private final Locator firstNameInput;
    private final Locator lastNameInput;
    private final Locator addressInput;
    private final Locator cityInput;
    private final Locator stateInput;
    private final Locator zipCodeInput;
    private final Locator phoneNumberInput;
    private final Locator ssnInput;
    private final Locator usernameInput;
    private final Locator passwordInput;
    private final Locator confirmPasswordInput;
    private final Locator signUpMessage;
    private final Locator submitRegistrationButton;
    private final Locator registrationSuccessfulMessage;
    private final Locator noFirstNameErrorMessage;
    private final Locator noLastNameErrorMessage;
    private final Locator noAddressErrorMessage;
    private final Locator noCityErrorMessage;
    private final Locator noStateErrorMessage;
    private final Locator noZipCodeErrorMessage;
    private final Locator noSsnErrorMessage;
    private final Locator noUsernameErrorMessage;
    private final Locator noPasswordErrorMessage;
    private final Locator noConfirmPasswordErrorMessage;
    private final Locator logOutLink;
    private final Locator registrationPageLink;


    public RegistrationPage(Page page) {

        this.page = page;
        this.firstNameInput = page.locator("#customer\\.firstName");
        this.lastNameInput = page.locator("#customer\\.lastName");
        this.addressInput = page.locator("#customer\\.address\\.street");
        this.cityInput = page.locator("#customer\\.address\\.city");
        this.stateInput = page.locator("#customer\\.address\\.state");
        this.zipCodeInput = page.locator("#customer\\.address\\.zipCode");
        this.phoneNumberInput = page.locator("#customer\\.phoneNumber");
        this.ssnInput = page.locator("#customer\\.ssn");
        this.usernameInput = page.locator("#customer\\.username");
        this.passwordInput = page.locator("#customer\\.password");
        this.confirmPasswordInput = page.locator("#repeatedPassword");
        this.signUpMessage = page.locator("#rightPanel h1.title");
        this.submitRegistrationButton = page.locator("#customerForm input[type=submit][value='Register']");
        this.registrationSuccessfulMessage = page.getByText("Your account was created successfully. You are now logged in.");
        this.noFirstNameErrorMessage = page.locator("#customer\\.firstName\\.errors");
        this.noLastNameErrorMessage = page.locator("#customer\\.lastName\\.errors");
        this.noAddressErrorMessage = page.locator("#customer\\.address\\.street\\.errors");
        this.noCityErrorMessage = page.locator("#customer\\.address\\.city\\.errors");
        this.noStateErrorMessage = page.locator("#customer\\.address\\.state\\.errors");
        this.noZipCodeErrorMessage = page.locator("#customer\\.address\\.zipCode\\.errors");
        this.noSsnErrorMessage = page.locator("#customer\\.ssn\\.errors");
        this.noUsernameErrorMessage = page.locator("#customer\\.username\\.errors");
        this.noPasswordErrorMessage = page.locator("#customer\\.password\\.errors");
        this.noConfirmPasswordErrorMessage = page.locator("#repeatedPassword\\.errors");
        this.logOutLink = page.getByText("Log Out");
        this.registrationPageLink = page.locator("//a[contains(text(),'Register')]");

    }

    //Expose Locators

    public Locator noFirstNameErrorMessage() {
        return noFirstNameErrorMessage;
    }

    public Locator noLastNameErrorMessage() {
        return noLastNameErrorMessage;
    }

    public Locator noAddressErrorMessage() {
        return noAddressErrorMessage;
    }

    public Locator noCityErrorMessage() {
        return noCityErrorMessage;
    }

    public Locator noStateErrorMessage() {
        return noStateErrorMessage;
    }

    public Locator noZipCodeErrorMessage() {
        return noZipCodeErrorMessage;
    }

    public Locator noSsnErrorMessage() {
        return noSsnErrorMessage;
    }

    public Locator noUsernameErrorMessage() {
        return noUsernameErrorMessage;
    }

    public Locator noPasswordErrorMessage() {
        return noPasswordErrorMessage;
    }

    public Locator noConfirmOrNoMatchPasswordErrorMessage() {
        return noConfirmPasswordErrorMessage;
    }

    public Locator signupMessage() {
        return signUpMessage;
    }

    public Locator submitRegistrationButton() {
        return submitRegistrationButton;
    }

    public Locator registrationSuccessfulMessage() {
        return registrationSuccessfulMessage;
    }

    //Page actions

    public void fillFirstName(String firstName) {
        firstNameInput.fill(firstName);
    }

    public void fillLastName(String lastName) {
        lastNameInput.fill(lastName);
    }

    public void fillAddress(String address) {
        addressInput.fill(address);
    }

    public void fillCity(String city) {
        cityInput.fill(city);
    }

    public void fillState(String state) {
        stateInput.fill(state);
    }

    public void fillZipCode(String zipCode) {
        zipCodeInput.fill(zipCode);
    }

    public void fillPhoneNumber(String phoneNumber) {
        phoneNumberInput.fill(phoneNumber);
    }

    public void fillSsn(String ssn) {
        ssnInput.fill(ssn);
    }

    public void fillUsername(String username) {
        usernameInput.fill(username);
    }

    public void fillPassword(String password) {
        passwordInput.fill(password);
    }

    public void fillConfirmPassword(String confirmPassword) {
        confirmPasswordInput.fill(confirmPassword);
    }

    public void fillRegistrationFieldsWithValidUser(User user) {
        String password = user.getPassword();
        firstNameInput.fill(user.getFirstName());
        lastNameInput.fill(user.getLastName());
        addressInput.fill(user.getAddress());
        cityInput.fill(user.getCity());
        stateInput.fill(user.getState());
        zipCodeInput.fill(user.getZipCode());
        phoneNumberInput.fill(user.getPhoneNumber());
        ssnInput.fill(user.getSsn());
        usernameInput.fill(user.getUsername());
        passwordInput.fill(password);
        confirmPasswordInput.fill(password);
    }

    public void clickSubmitRegistrationButton() {
        submitRegistrationButton.click();
    }

    public void registerValidUserAndLogInOrOut(User user, boolean stayLoggedIn) {
        fillRegistrationFieldsWithValidUser(user);
        clickSubmitRegistrationButton();
        assertThat(registrationSuccessfulMessage).isVisible();
        if (!stayLoggedIn) {
            clickLogOutButton();
            assertThat(registrationPageLink).isVisible();
        }
    }

    public void clickLogOutButton() {
        logOutLink.click();
    }

    public void fillRegistrationFieldsWithValidUserAndClearField(User user, RegistrationField field) {
        fillRegistrationFieldsWithValidUser(user);
        switch (field) {
            case FIRST_NAME -> firstNameInput.clear();
            case LAST_NAME -> lastNameInput.clear();
            case ADDRESS -> addressInput.clear();
            case CITY -> cityInput.clear();
            case STATE -> stateInput.clear();
            case ZIP_CODE -> zipCodeInput.clear();
            case PHONE -> phoneNumberInput.clear();
            case SSN -> ssnInput.clear();
            case USERNAME -> usernameInput.clear();
            case PASSWORD -> passwordInput.clear();
            case CONFIRM_PASSWORD -> confirmPasswordInput.clear();
        }
    }

    public void assertErrorOnRegistrationPage(Locator errorLocator, String expectedErrorMessage) {
        assertThat(errorLocator).isVisible();
        assertThat(errorLocator).hasText(expectedErrorMessage);
        assertThat(page).hasURL(NavigationPage.REGISTRATION_PAGE_URL);
    }

    public Locator getErrorMessageLocator(RegistrationField field){
        switch (field) {
            case FIRST_NAME -> {
                return noFirstNameErrorMessage();
            }
            case LAST_NAME -> {
                return noLastNameErrorMessage();
            }
            case ADDRESS -> {
                return noAddressErrorMessage();
            }
            case CITY -> {
                return noCityErrorMessage();
            }
            case STATE -> {
                return noStateErrorMessage();
            }
            case ZIP_CODE -> {
                return noZipCodeErrorMessage();
            }
            case SSN -> {
                return noSsnErrorMessage();
            }
            case USERNAME -> {
                return noUsernameErrorMessage();
            }
            case PASSWORD -> {
                return noPasswordErrorMessage();
            }
            case CONFIRM_PASSWORD -> {
                return noConfirmOrNoMatchPasswordErrorMessage();
            }
        }
        return null;
    }
}
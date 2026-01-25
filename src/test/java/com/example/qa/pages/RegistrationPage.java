package com.example.qa.pages;

import com.example.qa.config.TestConfig;
import com.example.qa.models.User;
import com.example.qa.models.UserFactory;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.Locator;

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
    private final Locator registrationPageLink;
    private final Locator signUpMessage;
    private final Locator submitRegistrationButton;
    private final Locator registrationSuccessfulMessage;
    private final Locator registrationFailedMessage;

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
        this.registrationPageLink = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Register"));
        this.signUpMessage = page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Signing up is easy!"));
        this.submitRegistrationButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("REGISTER"));
        this.registrationSuccessfulMessage = page.getByText("Your account was created successfully. You are now logged in.");
        this.registrationFailedMessage = page.locator("#customer\\.username\\.errors");

    }

    public void navigateToRegistrationPage() {
        page.navigate(TestConfig.getBaseUrl());
        registrationPageLink.click();
    }

    public boolean isRegistrationMessageDisplayed() {
        return signUpMessage.isVisible();
    }

    public boolean isSubmitRegistrationButtonDisplayed() {
        return submitRegistrationButton.isVisible();
    }

    public boolean isRegistrationSuccessfulDisplayed() {
        return registrationSuccessfulMessage.isVisible();
    }

    public boolean isRegistrationFailedDisplayed() {
        return registrationFailedMessage.isVisible() &&
                registrationFailedMessage.allTextContents().contains("This username already exists.");
    }

    public void fillUsername(String username) {
        usernameInput.fill(username);
    }

    public void registerValidUser(User user) {
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
        submitRegistrationButton.click();
    }
}
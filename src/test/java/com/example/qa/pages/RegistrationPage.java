package com.example.qa.pages;

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
    private final Locator registerButton;

    public RegistrationPage(Page page) {
        this.page = page;
        this.firstNameInput = page.locator("#customer\\.firstName");
        this.lastNameInput = page.locator("#customer\\.lastName");
        this.addressInput = page.locator("customer\\.address\\.street");
        this.cityInput = page.locator("customer\\.address\\.city");
        this.stateInput = page.locator("customer\\.address\\.state");
        this.zipCodeInput = page.locator("customer\\.address\\.zipCode");
        this.phoneNumberInput = page.locator("#customer\\.phoneNumber");
        this.ssnInput = page.locator("#customer\\.ssn");
        this.usernameInput = page.locator("#customer\\.username");
        this.passwordInput = page.locator("#customer\\.password");
        this.confirmPasswordInput = page.locator("#repeatedPassword");
        this.registerButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Register"));
    }

    public void fillUsername(String username) {
        usernameInput.fill(username);
    }

    public void registerValidUser(
            String firstName,
            String lastName,
            String address,
            String city,
            String state,
            String zipCode,
            String phoneNumber,
            String ssn,
            String username,
            String password) {
        firstNameInput.fill(firstName);
        lastNameInput.fill(lastName);
        addressInput.fill(address);
        cityInput.fill(city);
        stateInput.fill(state);
        zipCodeInput.fill(zipCode);
        phoneNumberInput.fill(phoneNumber);
        ssnInput.fill(ssn);
        usernameInput.fill(username);
        passwordInput.fill(password);
        registerButton.click();
    }

}
package com.example.qa.pages;

import com.example.qa.api.dtos.User;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Locator;

public class LoginPage {

    private final Locator usernameInput;
    private final Locator passwordInput;
    private final Locator logInButton;
    private final Locator welcomeMessage;

    public LoginPage(Page page) {
        this.usernameInput = page.locator("input[name='username']");
        this.passwordInput = page.locator("input[name='password']");
        this.logInButton = page.locator("input[type=submit]");
        this.welcomeMessage = page.locator(".smallText");
    }

    //Expose locators
    public Locator usernameInput() {
        return usernameInput;
    }

    public Locator passwordInput() {
        return passwordInput;
    }

    public Locator logInButton() {
        return logInButton;
    }

    public Locator welcomeMessage() {
        return welcomeMessage;
    }

    //Page actions
    public void fillUsername(String username) {
        usernameInput.fill(username);
    }

    public void fillPassword(String password) {
        passwordInput.fill(password);
    }

    public void clickLogIn() {
        logInButton.click();
    }

    public void logIn(User user) {
        usernameInput.fill(user.username());
        passwordInput.fill(user.password());
        logInButton.click();
    }
}

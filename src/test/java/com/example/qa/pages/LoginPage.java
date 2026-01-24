package com.example.qa.pages;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.example.qa.config.TestConfig;
import com.microsoft.playwright.Locator;

public class LoginPage {
    private final Page page;
    private final Locator usernameInput;
    private final Locator passwordInput;
    private final Locator loginButton;
    private final Locator registerButton;

    public LoginPage(Page page) {
        this.page = page;
        this.usernameInput = page.locator("input[name='username']");
        this.passwordInput = page.locator("input[name='password']");
        this.loginButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Log In"));
        this.registerButton = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Register"));
    }

    public void navigate() {
        page.navigate(TestConfig.getBaseUrl());
    }

    public void login(String username, String password) {
        usernameInput.fill(username);
        passwordInput.fill(password);
        loginButton.click();
    }
}

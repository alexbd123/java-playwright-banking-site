package com.example.qa.tests;

import org.junit.jupiter.api.Test;
import com.microsoft.playwright.*;

import com.example.qa.pages.LoginPage;

public class LoginPageTests {

    @Test
    void userCanLogIn() {
        try (Playwright playwright = Playwright.create()) {

            Browser browser = playwright.chromium().launch();
            Page page = browser.newPage();

            LoginPage loginPage = new LoginPage(page);

            loginPage.navigate();
            loginPage.login("abdunn123", "KsZxc!qHmsUJ9");
        }
    }

}

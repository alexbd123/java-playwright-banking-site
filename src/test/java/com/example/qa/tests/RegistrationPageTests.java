package com.example.qa.tests;

import com.example.qa.pages.RegistrationPage;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RegistrationPageTests {

    private Playwright playwright;
    private Browser browser;
    private Page page;
    private RegistrationPage registrationPage;


    @BeforeEach
    void setUp() {
        playwright = Playwright.create();

        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions().setHeadless(false)
        );

        page = browser.newPage();

        registrationPage = new RegistrationPage(page);

        registrationPage.navigateToRegistrationPage();
    }

    @AfterEach
    void tearDown() {
        browser.close();
        playwright.close();
    }

    @Test
    void userCanNavigateToRegistrationPage() {
        Assertions.assertTrue(registrationPage.isRegistrationMessageDisplayed());
        Assertions.assertTrue(registrationPage.isSubmitRegistrationButtonDisplayed());
    }
}
package com.example.qa.tests.base_tests;

import com.example.qa.config.TestConfig;
import com.example.qa.api.dtos.User;
import com.example.qa.models.UserFactory;
import com.example.qa.pages.NavigationPage;
import com.example.qa.pages.RegistrationPage;
import com.microsoft.playwright.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

public abstract class BaseTest {

    protected static Playwright playwright;
    protected static Browser browser;
    protected BrowserContext context;
    protected Page page;
    protected User user;
    protected RegistrationPage registrationPage;
    protected NavigationPage goTo;

    @BeforeAll
    static void globalSetUp() {
        playwright = Playwright.create();
        boolean headless = TestConfig.getHeadless();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(headless));
    }

    @AfterAll
    static void globalTearDown() {
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
    }

    @BeforeEach
    void setUp() {
        createContextAndPage();
        navigateToRegistrationAndInitialisePages();
        createValidUser();
    }

    void createContextAndPage() {
        context = browser.newContext();
        page = context.newPage();
    }

    void navigateToRegistrationAndInitialisePages() {
        page.navigate(TestConfig.getBaseUrl());
        goTo = new NavigationPage(page);
        registrationPage = new RegistrationPage(page);
        goTo.registrationPage();
    }

    void createValidUser() {
        user = UserFactory.validRandomUser();
    }

    @AfterEach
    void closeContext() {
        if (context != null) context.close();
    }
}
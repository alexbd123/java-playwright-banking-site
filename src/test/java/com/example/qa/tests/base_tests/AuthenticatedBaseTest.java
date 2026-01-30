package com.example.qa.tests.base_tests;

import com.example.qa.config.TestConfig;
import com.example.qa.models.User;
import com.example.qa.models.UserFactory;
import com.example.qa.pages.*;
import com.microsoft.playwright.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

public abstract class AuthenticatedBaseTest {

    protected static Playwright playwright;
    protected static Browser browser;
    protected BrowserContext context;
    protected Page page;
    protected User user;
    protected OpenNewAccountPage openNewAccountPage;
    protected NavigationPage goTo;
    protected RegistrationPage registrationPage;
    protected AccountsOverviewPage accountsOverviewPage;
    protected LoginPage loginPage;

    @BeforeAll
    static void globalSetUp() {
        playwright = Playwright.create();
        boolean headless = Boolean.parseBoolean(System.getProperty("HEADLESS",
                System.getenv().getOrDefault("HEADLESS", "true")));
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
        navigateToSiteAndInitialisePages();
        createAndRegisterUser();
    }

    protected void createContextAndPage() {
        context = browser.newContext();
        page = context.newPage();
    }

    protected void navigateToSiteAndInitialisePages() {
        page.navigate(TestConfig.getBaseUrl());
        goTo = new NavigationPage(page);
        registrationPage = new RegistrationPage(page);
        openNewAccountPage = new OpenNewAccountPage(page);
        accountsOverviewPage = new AccountsOverviewPage(page);
        loginPage = new LoginPage(page);
    }

    protected void createAndRegisterUser() {
        goTo.registrationPage();
        user = UserFactory.validRandomUser();
        registrationPage.registerValidUserAndLogInOrOut(user, true);
    }

    @AfterEach
    void closeContext() {
        if (context != null) context.close();
    }
}
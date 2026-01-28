package com.example.qa.tests;

import com.example.qa.config.TestConfig;
import com.example.qa.pages.NavigationPage;
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

    @BeforeAll
    static void globalSetUp() {
        playwright = Playwright.create();
        boolean headless = Boolean.parseBoolean(System.getProperty("HEADLESS",
                System.getenv().getOrDefault("HEADLESS", "false")));
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(headless));
    }

    @AfterAll
    static void globalTearDown() {
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
    }

    @BeforeEach
    void createContextAndPageAndGoToRegistration() {
        context = browser.newContext();
        page = context.newPage();
        page.navigate(TestConfig.getBaseUrl());
        NavigationPage goTo = new NavigationPage(page);
        goTo.registrationPage();
    }

    @AfterEach
    void closeContext() {
        if (context != null) context.close();
    }
}
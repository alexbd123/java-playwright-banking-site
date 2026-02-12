package com.example.qa.tests.base_tests;

import com.example.qa.api.context.CustomerContext;
import com.example.qa.api.context.CustomerContextBuilder;
import com.example.qa.config.TestConfig;
import com.example.qa.models.User;
import com.example.qa.models.UserFactory;
import com.example.qa.pages.*;
import com.microsoft.playwright.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.util.HashMap;
import java.util.Map;

public abstract class AuthenticatedBaseTest {

    protected static Playwright playwright;
    protected static Browser browser;
    protected BrowserContext context;
    protected Page page;
    protected static User user;
    protected OpenNewAccountPage openNewAccountPage;
    protected NavigationPage goTo;
    protected RegistrationPage registrationPage;
    protected AccountsOverviewPage accountsOverviewPage;
    protected LoginPage loginPage;
    protected TransferFundsPage transferFundsPage;
    protected FindTransactionsPage findTransactionsPage;
    protected TransactionDetailsPage transactionDetailsPage;
    protected static APIRequestContext request;
    protected static String requestContextState;
    protected static CustomerContext customerContext;

    @BeforeAll
    static void globalSetUp() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(TestConfig.getHeadless()));
        createAPIRequestContext();
        createAndRegisterUserInTempContext();
        customerContext = new CustomerContextBuilder(request).buildContextFor(user);
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
    }

    @AfterEach
    void closeContext() {
        if (context != null) context.close();
    }

    static void createAPIRequestContext() {
        Map<String, String> headers = new HashMap<>();
        headers.put("accept", "application/json");
        request = playwright.request().newContext(new APIRequest.NewContextOptions()
                .setBaseURL(TestConfig.getApiBaseUrl())
                .setExtraHTTPHeaders(headers));
    }

    static void createAndRegisterUserInTempContext() {
        BrowserContext tempCtx = browser.newContext();
        Page tempPage = tempCtx.newPage();
        RegistrationPage tempRegistration = new RegistrationPage(tempPage);
        NavigationPage tempGoTo = new NavigationPage(tempPage);
        tempPage.navigate(TestConfig.getBaseUrl());
        tempGoTo.registrationPage();
        user = UserFactory.validRandomUser();
        tempRegistration.registerValidUserAndLogInOrOut(user, true);
        requestContextState = tempCtx.storageState();
        tempCtx.close();
    }

    protected void createContextAndPage() {
        context = browser.newContext(new Browser.NewContextOptions().setStorageState(requestContextState));
        page = context.newPage();
    }

    protected void navigateToSiteAndInitialisePages() {
        goTo = new NavigationPage(page);
        registrationPage = new RegistrationPage(page);
        openNewAccountPage = new OpenNewAccountPage(page);
        accountsOverviewPage = new AccountsOverviewPage(page);
        loginPage = new LoginPage(page);
        transferFundsPage = new TransferFundsPage(page);
        findTransactionsPage = new FindTransactionsPage(page);
        transactionDetailsPage = new TransactionDetailsPage(page);
        page.navigate(TestConfig.getBaseUrl());
    }

}
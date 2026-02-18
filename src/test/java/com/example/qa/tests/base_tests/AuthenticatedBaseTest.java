package com.example.qa.tests.base_tests;

import com.example.qa.api.context.CustomerContext;
import com.example.qa.api.context.builders.CustomerContextBuilder;
import com.example.qa.api.helpers.ApiHelper;
import com.example.qa.tests.ui.TestHelper;
import com.example.qa.config.TestConfig;
import com.example.qa.api.dtos.User;
import com.example.qa.api.context.builders.UserFactory;
import com.example.qa.pages.*;
import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;

import java.util.HashMap;
import java.util.Map;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class AuthenticatedBaseTest {

    protected Playwright playwright;
    protected Browser browser;

    protected BrowserContext context;
    protected Page page;

    protected User user;

    protected OpenNewAccountPage openNewAccountPage;
    protected NavigationPage goTo;
    protected RegistrationPage registrationPage;
    protected AccountsOverviewPage accountsOverviewPage;
    protected LoginPage loginPage;
    protected TransferFundsPage transferFundsPage;
    protected FindTransactionsPage findTransactionsPage;
    protected TransactionDetailsPage transactionDetailsPage;
    protected AccountActivityPage accountActivityPage;

    protected ApiHelper beHelper;
    protected TestHelper feHelper;
    protected APIRequestContext request;
    protected String requestContextState;
    protected CustomerContext customerContext;

    @BeforeAll
    void globalSetUp() {
        // Playwright + browser shared per class
        playwright = Playwright.create();
        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions().setHeadless(TestConfig.getHeadless())
        );

        // API
        createAPIRequestContext();
        createAndRegisterUserInTempContext();

        // Customer context + api helper
        beHelper = new ApiHelper(request);
        customerContext = new CustomerContextBuilder(request).buildContextFor(user);
    }

    @AfterAll
    void globalTearDown() {
        if (request != null) request.dispose();
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
    }

    @BeforeEach
    void setUp() {
        createContextAndPage();
        initPages();
        page.navigate(TestConfig.getBaseUrl());
    }

    @AfterEach
    void closeContext() {
        if (context != null) context.close();
    }

    void createAPIRequestContext() {
        Map<String, String> headers = new HashMap<>();
        headers.put("accept", "application/json");

        request = playwright.request().newContext(
                new APIRequest.NewContextOptions()
                        .setBaseURL(TestConfig.getApiBaseUrl())
                        .setExtraHTTPHeaders(headers)
        );
    }

    void createAndRegisterUserInTempContext() {
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

    void createContextAndPage() {
        context = browser.newContext(
                new Browser.NewContextOptions().setStorageState(requestContextState)
        );
        page = context.newPage();
    }

    void initPages() {
        goTo = new NavigationPage(page);
        registrationPage = new RegistrationPage(page);
        openNewAccountPage = new OpenNewAccountPage(page);
        accountsOverviewPage = new AccountsOverviewPage(page);
        loginPage = new LoginPage(page);
        transferFundsPage = new TransferFundsPage(page);
        findTransactionsPage = new FindTransactionsPage(page);
        transactionDetailsPage = new TransactionDetailsPage(page);
        accountActivityPage = new AccountActivityPage(page);
        feHelper = new TestHelper(page);
    }
}
package com.example.qa.tests.base_tests;

import com.example.qa.api.ApiHelper;
import com.example.qa.api.clients.CustomerAPI;
import com.example.qa.api.dtos.CustomerDto;
import com.example.qa.config.TestConfig;
import com.example.qa.models.User;
import com.example.qa.models.UserFactory;
import com.example.qa.pages.*;
import com.fasterxml.jackson.core.JsonProcessingException;
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
    protected static Integer customerId;
    protected static Integer originalAccountId;
    protected OpenNewAccountPage openNewAccountPage;
    protected NavigationPage goTo;
    protected RegistrationPage registrationPage;
    protected AccountsOverviewPage accountsOverviewPage;
    protected LoginPage loginPage;
    protected TransferFundsPage transferFundsPage;
    protected static APIRequestContext request;
    protected static String requestContextState;

    @BeforeAll
    static void globalSetUp() throws JsonProcessingException {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(TestConfig.getHeadless()));
        createAPIRequestContext();
        createAndRegisterUserInTempContext();
        fetchAndCacheCustomerIdOnce();
        fetchAndCacheOriginalAccountIdOnce();
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
        page.navigate(TestConfig.getBaseUrl());
    }

    private static void fetchAndCacheCustomerIdOnce() throws JsonProcessingException {
        if (customerId != null) return;
        CustomerAPI customerRequest = new CustomerAPI(request);
        ApiHelper helper = new ApiHelper();
        APIResponse loginRequestResponse = customerRequest.sendGetRequestToLogIn(user.getUsername(), user.getPassword());
        customerId = helper.getCustomerIdFromLoginRequestResponse(loginRequestResponse);
        if (customerId == null) {
            throw new IllegalStateException("Login API returned null response");
        }
    }

    private static void fetchAndCacheOriginalAccountIdOnce() throws JsonProcessingException {
        if (originalAccountId != null) return;
        CustomerAPI customerRequest = new CustomerAPI(request);
        ApiHelper helper = new ApiHelper();
        APIResponse loginRequestResponse = customerRequest.sendGetRequestForCustomerAccountsInfo(customerId);
        originalAccountId = helper.getOriginalAccountIdFromResponse(loginRequestResponse);
        if (originalAccountId == null) {
            throw new IllegalStateException("Login API returned null response");
        }
    }

}
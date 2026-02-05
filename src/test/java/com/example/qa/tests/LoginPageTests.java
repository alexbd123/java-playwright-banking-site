package com.example.qa.tests;

import com.example.qa.tests.base_tests.AuthenticatedBaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.example.qa.api.clients.CustomerAPI;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class LoginPageTests extends AuthenticatedBaseTest {

    CustomerAPI customerRequest;

    @BeforeEach
    void initApi() {
        customerRequest = new CustomerAPI(request);
    }

    @Test
    void registeredValidUserCanLogIn() {
        loginPage.logIn(user);
        var welcomeMessage = loginPage.welcomeMessage();
        assertThat(welcomeMessage).isVisible();
        assertThat(welcomeMessage).hasText("Welcome " + user.getFirstName() + " " + user.getLastName());
    }
}
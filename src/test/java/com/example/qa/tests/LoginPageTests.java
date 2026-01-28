package com.example.qa.tests;

import com.example.qa.models.User;
import com.example.qa.models.UserFactory;
import com.example.qa.pages.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class LoginPageTests extends BaseTest {

    private LoginPage loginPage;
    private RegistrationPage registrationPage;
    private User user;

    @BeforeEach
    public void setUp() {
        registrationPage = new RegistrationPage(page);
        user = UserFactory.validRandomUser();
        registrationPage.registerValidUserAndLogInOrOut(user, false);
        loginPage = new LoginPage(page);
    }

    @Test
    void registeredValidUserCanLogIn() {
        loginPage.fillUsername(user.getUsername());
        loginPage.fillPassword(user.getPassword());
        loginPage.clickLogIn();

        var welcomeMessage = loginPage.welcomeMessage();
        assertThat(welcomeMessage).isVisible();
        assertThat(welcomeMessage).hasText("Welcome " + user.getFirstName() + " " + user.getLastName());
        assertThat(page).hasURL(LoginPage.LOGGED_IN_URL);
        }
    }

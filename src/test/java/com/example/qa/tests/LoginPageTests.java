package com.example.qa.tests;

import com.example.qa.models.User;
import com.example.qa.models.UserFactory;
import com.example.qa.pages.NavigationPage;
import com.example.qa.pages.RegistrationPage;
import org.junit.jupiter.api.Test;
import com.example.qa.pages.LoginPage;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class LoginPageTests extends BaseTest {

    public LoginPage loginPage;
    public RegistrationPage registrationPage;
    public NavigationPage goTo;

    @Test
    void registeredValidUserCanLogIn() {
        goTo = new NavigationPage(page);
        goTo.registrationPage();
        registrationPage = new RegistrationPage(page);
        User user = UserFactory.validRandomUser();
        registrationPage.registerValidUserAndLogInOrOut(user, false);

        loginPage = new LoginPage(page);
        assertThat(loginPage.usernameInput()).isVisible();

        loginPage.fillUsername(user.getUsername());
        loginPage.fillPassword(user.getPassword());
        loginPage.clickLogIn();

        var welcomeMessage = loginPage.welcomeMessage();
        assertThat(welcomeMessage).isVisible();
        assertThat(welcomeMessage).hasText("Welcome " + user.getFirstName() + " " + user.getLastName());
        assertThat(page).hasURL(LoginPage.LOGGED_IN_URL);
        }
    }

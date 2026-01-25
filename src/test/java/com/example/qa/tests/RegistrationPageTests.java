package com.example.qa.tests;

import com.example.qa.models.UserFactory;
import com.example.qa.pages.RegistrationPage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RegistrationPageTests extends BaseTest {

    public RegistrationPage registrationPage;


    @BeforeEach
    void openRegistration() {
        registrationPage = new RegistrationPage(page);
        registrationPage.navigateToRegistrationPage();
    }

    @Test
    void userCanNavigateToRegistrationPage() {
        Assertions.assertTrue(registrationPage.isRegistrationMessageDisplayed());
        Assertions.assertTrue(registrationPage.isSubmitRegistrationButtonDisplayed());
    }

    @Test
    void validUserCanSubmitRegistration() {
        registrationPage.registerValidUser(UserFactory.validRandomUser());
        Assertions.assertTrue(registrationPage.isRegistrationSuccessfulDisplayed());
    }

    @Test
    void invalidUserCannotSubmitRegistration() {
        registrationPage.registerValidUser(UserFactory.invalidRandomUser());
        Assertions.assertTrue(registrationPage.isRegistrationFailedDisplayed());
    }
}
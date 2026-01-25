package com.example.qa.tests;

import org.junit.jupiter.api.Test;
import com.microsoft.playwright.*;

import com.example.qa.pages.LoginPage;

public class LoginPageTests extends BaseTest {

    @Test
    void userCanLogIn() {
            LoginPage loginPage = new LoginPage(page);
            loginPage.login("abdunn123", "KsZxc!qHmsUJ9");
        }
    }

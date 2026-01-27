package com.example.qa.tests;

import com.example.qa.models.User;
import com.example.qa.models.UserFactory;
import com.example.qa.pages.NavigationPage;
import com.example.qa.pages.OpenNewAccountPage;
import com.example.qa.pages.RegistrationPage;
import org.junit.jupiter.api.Test;

public class OpenNewAccountTests extends BaseTest {

    public RegistrationPage registrationPage;
    public NavigationPage goTo;
    public OpenNewAccountPage openNewAccountPage;

    @Test
    public void userCanOpenNewCheckingAccount() {
        goTo = new NavigationPage(page);
        goTo.registrationPage();
        registrationPage = new RegistrationPage(page);
        User user = UserFactory.validRandomUser();
        registrationPage.registerValidUserAndLogInOrOut(user, true);

        goTo = new NavigationPage(page);
        goTo.openNewAccount();
        openNewAccountPage = new OpenNewAccountPage(page);

        openNewAccountPage.openAccountTypeDropdown();
        openNewAccountPage.checkingOptionInDropdown().click();

    }
}

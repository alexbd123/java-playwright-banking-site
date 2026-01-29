package com.example.qa.tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;


import com.example.qa.enums.AccountTypes;
import com.example.qa.models.User;
import com.example.qa.models.UserFactory;
import com.example.qa.pages.NavigationPage;
import com.example.qa.pages.OpenNewAccountPage;
import com.example.qa.pages.RegistrationPage;
import com.example.qa.pages.TransferFundsPage;

public class TransferFundsTests extends BaseTest {

    private NavigationPage goTo;
    private OpenNewAccountPage openNewAccountPage;
    private String newCheckingAccount;
    private String newSavingsAccount;

    @BeforeEach
    public void setUp() {
        RegistrationPage registrationPage = new RegistrationPage(page);
        openNewAccountPage = new OpenNewAccountPage(page);
        goTo = new NavigationPage(page);
        User user = UserFactory.validRandomUser();
        registrationPage.registerValidUserAndLogInOrOut(user, true);
        goTo.openNewAccount();
        openNewAccountPage.selectAccountTypeAndOpenNewAccount(AccountTypes.CHECKING);
        newCheckingAccount = openNewAccountPage.getAccountNumber();
        goTo.openNewAccount();
        openNewAccountPage.selectAccountTypeAndOpenNewAccount(AccountTypes.SAVINGS);
        newSavingsAccount = openNewAccountPage.getAccountNumber();
        goTo.transferFunds();
    }

    @ParameterizedTest(name = "User can send 5$ from original account into new {0} account")
    @EnumSource(AccountTypes.class)
    void userCanSend5DollarsToNewAccount(AccountTypes accountType) {
        TransferFundsPage transferFundsPage = new TransferFundsPage(page);
        transferFundsPage.enterAmountIntoAmountInput("5");
        transferFundsPage.selectAccountToSendTo(accountType == AccountTypes.CHECKING
             ? newCheckingAccount : newSavingsAccount);
        transferFundsPage.clickTransferButton();
        assertThat(transferFundsPage.getSuccessMessage()).isVisible();     
    }  
}

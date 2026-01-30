package com.example.qa.tests;

import com.example.qa.tests.base_tests.AuthenticatedBaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;


import com.example.qa.enums.AccountTypes;
import com.example.qa.pages.TransferFundsPage;

public class TransferFundsTests extends AuthenticatedBaseTest {

    private String newCheckingAccount;
    private String newSavingsAccount;

    @BeforeEach
    void setUpNewAccountsForTests() {
        goTo.openNewAccount();
        newCheckingAccount = openNewAccountPage.openNewAccountAndReturnAccountNumber(AccountTypes.CHECKING);
        goTo.openNewAccount();
        newSavingsAccount = openNewAccountPage.openNewAccountAndReturnAccountNumber(AccountTypes.SAVINGS);
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

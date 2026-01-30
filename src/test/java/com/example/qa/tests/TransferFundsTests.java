package com.example.qa.tests;

import com.example.qa.tests.base_tests.AuthenticatedBaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import com.example.qa.enums.AccountTypes;

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

    @ParameterizedTest(name = "User can transfer funds from original account into new {0} account")
    @EnumSource(AccountTypes.class)
    void userCanTransferFundsToNewAccount(AccountTypes accountType) { 
        transferFundsPage.transferFunds(
            transferFundsPage.getOriginalAccountNumber(),
            accountType == AccountTypes.CHECKING ? newCheckingAccount : newSavingsAccount,
              5);
        assertThat(transferFundsPage.getSuccessMessage()).isVisible();     
    }  

    @ParameterizedTest(name = "User can transfer funds from new {0} account into original account")
    @EnumSource(AccountTypes.class)
    void userCanTransferFundsToOriginalAccount(AccountTypes accountType) { 
        transferFundsPage.transferFunds(accountType == AccountTypes.CHECKING
             ? newCheckingAccount : newSavingsAccount,
             transferFundsPage.getOriginalAccountNumber(),
              5);
        assertThat(transferFundsPage.getSuccessMessage()).isVisible();     
    }  
}

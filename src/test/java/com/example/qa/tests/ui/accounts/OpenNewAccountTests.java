package com.example.qa.tests.ui.accounts;

import com.example.qa.api.clients.AccountActionsAPI;
import com.example.qa.api.dtos.AccountDto;
import com.example.qa.enums.AccountTypes;
import com.example.qa.tests.base_tests.AuthenticatedBaseTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OpenNewAccountTests extends AuthenticatedBaseTest {

    private final BigDecimal DEFAULT_BALANCE = BigDecimal.valueOf(100);

    protected static AccountActionsAPI accountActionsAPI;
    protected AccountDto originalCheckingAccount;
    protected int originalCheckingAccountId;

    @BeforeAll
    void initApiClients() {
        accountActionsAPI = new AccountActionsAPI(request);
        originalCheckingAccount = customerContext.getOriginalAccount();
        originalCheckingAccountId = originalCheckingAccount.id();
    }

    @ParameterizedTest(name = "User can open new {0} account and see it in overview")
    @EnumSource(AccountTypes.class)
    void userCanOpenNewAccount(AccountTypes accountType) {
        goTo.openNewAccount();
        openNewAccountPage.openNewAccount(accountType, originalCheckingAccountId);
        assertThat(openNewAccountPage.successfullyOpenedAccountMessage()).isVisible();
        int newAccountId = openNewAccountPage.getNewAccountNumber();
        feHelper.goToOverviewAndWaitForTableVisibility();
        accountsOverviewPage.assertThatBalanceIsVisibleAndAmountIsCorrect(newAccountId, DEFAULT_BALANCE);
    }

    @ParameterizedTest(name = "User can open new {0} account using new {1} account and see it in overview")
    @MethodSource("provideAccountTypeTestData")
    void userCanUseNewAccountToOpenNewAccount(AccountTypes newAccountType1, AccountTypes newAccountType2) {
        AccountDto newAccount = accountActionsAPI.createNewAccount(
                customerContext.getCustomerId(),
                newAccountType1,
                originalCheckingAccountId);
        int newAccountId = newAccount.id();

        goTo.openNewAccount();
        openNewAccountPage.openNewAccount(newAccountType2, newAccountId);
        assertThat(openNewAccountPage.successfullyOpenedAccountMessage()).isVisible();
        int accountCreatedFromNewAccountId = openNewAccountPage.getNewAccountNumber();
        feHelper.goToOverviewAndWaitForTableVisibility();

        accountsOverviewPage.assertThatBalanceIsVisibleAndAmountIsCorrect(accountCreatedFromNewAccountId, DEFAULT_BALANCE);
    }

    //Method Sources

    private static Stream<Arguments> provideAccountTypeTestData() {
        return Stream.of(
                Arguments.of(AccountTypes.CHECKING, AccountTypes.CHECKING),
                Arguments.of(AccountTypes.CHECKING, AccountTypes.SAVINGS),
                Arguments.of(AccountTypes.SAVINGS, AccountTypes.CHECKING),
                Arguments.of(AccountTypes.SAVINGS, AccountTypes.SAVINGS)
        );
    }

}

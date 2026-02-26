package com.example.qa.tests.ui.accounts;

import com.example.qa.api.clients.AccountActionsAPI;
import com.example.qa.api.dtos.AccountDto;
import com.example.qa.enums.AccountType;
import com.example.qa.pages.ui_dtos.AccountActivityDto;
import com.example.qa.tests.base_tests.AuthenticatedBaseTest;
import com.example.qa.tests.test_data.test_data_factories.AccountsDataFactory;
import com.example.qa.tests.test_data.test_data_records.NewAccountsForTests;
import org.junit.jupiter.api.Assertions;
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
    protected int originalCustomerId;
    protected AccountsDataFactory accountsDataFactory;

    @BeforeAll
    void initApiClients() {
        accountActionsAPI = new AccountActionsAPI(request);
        originalCheckingAccount = customerContext.originalAccount();
        originalCheckingAccountId = originalCheckingAccount.id();
        originalCustomerId = customerContext.customerId();
        accountsDataFactory = new AccountsDataFactory(request);
    }

    @ParameterizedTest(name = "User can open new {0} account and see it in overview")
    @EnumSource(value = AccountType.class, mode = EnumSource.Mode.EXCLUDE, names = "LOAN")
    void userCanOpenNewAccount(AccountType accountType) {
        goTo.openNewAccount();
        openNewAccountPage.openNewAccount(accountType, originalCheckingAccountId);
        assertThat(openNewAccountPage.successfullyOpenedAccountMessage()).isVisible();
        int newAccountId = openNewAccountPage.getNewAccountNumber();
        BigDecimal newAccountBalance = beHelper.retrieveAccountBalance(originalCustomerId, newAccountId);
        AccountActivityDto expectedAccount = new AccountActivityDto(
                newAccountId,
                accountType,
                newAccountBalance,
                newAccountBalance
        );

        feHelper.goToOverviewAndWaitForTableVisibility();
        accountsOverviewPage.assertBalanceVisibility(newAccountId, DEFAULT_BALANCE);
        accountsOverviewPage.goToAccountActivity(newAccountId);
        AccountActivityDto actualAccount = accountActivityPage.toDto();

        Assertions.assertEquals(expectedAccount, actualAccount);
    }

    @ParameterizedTest(name = "User can open new {0} account using new {1} account and see it in overview")
    @MethodSource("provideAccountTypeTestData")
    void userCanUseNewAccountToOpenNewAccount(AccountType newAccountType1, AccountType newAccountType2) {
        NewAccountsForTests testData = accountsDataFactory.createOneNewAccountForTest(
                customerContext.customerId(),
                customerContext.originalAccount().id(),
                newAccountType1
        );
        goTo.openNewAccount();
        openNewAccountPage.openNewAccount(newAccountType2, testData.account1().id());
        assertThat(openNewAccountPage.successfullyOpenedAccountMessage()).isVisible();
        
        int accountCreatedFromNewAccountId = openNewAccountPage.getNewAccountNumber();
        BigDecimal newAccountBalance = beHelper.retrieveAccountBalance(originalCustomerId, accountCreatedFromNewAccountId);
        AccountActivityDto expectedAccount = new AccountActivityDto(
                accountCreatedFromNewAccountId,
                newAccountType2,
                newAccountBalance,
                newAccountBalance
        );
        feHelper.goToOverviewAndWaitForTableVisibility();
        accountsOverviewPage.assertBalanceVisibility(accountCreatedFromNewAccountId, DEFAULT_BALANCE);
        accountsOverviewPage.goToAccountActivity(accountCreatedFromNewAccountId);
        AccountActivityDto actualAccount = accountActivityPage.toDto();

        Assertions.assertEquals(expectedAccount, actualAccount);
    }

    //Method Sources

    private static Stream<Arguments> provideAccountTypeTestData() {
        return Stream.of(
                Arguments.of(AccountType.CHECKING, AccountType.CHECKING),
                Arguments.of(AccountType.CHECKING, AccountType.SAVINGS),
                Arguments.of(AccountType.SAVINGS, AccountType.CHECKING),
                Arguments.of(AccountType.SAVINGS, AccountType.SAVINGS)
        );
    }

}

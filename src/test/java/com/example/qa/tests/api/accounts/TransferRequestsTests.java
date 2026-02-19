package com.example.qa.tests.api.accounts;

import com.example.qa.api.clients.AccountActionsAPI;
import com.example.qa.api.dtos.AccountDto;
import com.example.qa.api.dtos.TransactionDto;
import com.example.qa.api.http.ResponsesFactory;
import com.example.qa.enums.TransactionTypes;
import com.example.qa.tests.base_tests.AuthenticatedBaseTest;
import com.example.qa.tests.test_data.test_data_factories.AccountsDataFactory;
import com.example.qa.tests.test_data.test_data_records.NewAccountsForTests;
import com.example.qa.tests.utils.TimeUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import com.example.qa.enums.AccountTypes;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TransferRequestsTests extends AuthenticatedBaseTest {

    private final String TRANSFER_DESCRIPTION = "Funds Transfer Received";;

    protected AccountActionsAPI accountActionsAPI;
    protected TimeUtils time;
    protected AccountDto originalCheckingAccount;
    protected ResponsesFactory responsesFactory;
    protected AccountsDataFactory accountsDataFactory;
    protected int originalAccountId;
    protected int originalCustomerId;


    @BeforeAll
    void initApiAndGetContext() {
        accountActionsAPI = new AccountActionsAPI(request);
        time = new TimeUtils();
        responsesFactory = new ResponsesFactory();
        originalCheckingAccount = customerContext.originalAccount();
        originalCustomerId = customerContext.customerId();
        originalAccountId = originalCheckingAccount.id();
        accountsDataFactory = new AccountsDataFactory(request);
    }

    @ParameterizedTest(name = "Successful {0} transfer POST request from {2} account to {3} account")
    @MethodSource("provideTransferAmountAndAccountTypes")
    void fundsCanBeTransferredBetweenAccounts(String amountDescription, BigDecimal transferAmount, AccountTypes fromAccountType, AccountTypes toAccountType) {
        //Arrange: Create two accounts for transfer, get pre-transfer expected balance and transfer dto
        NewAccountsForTests testData = accountsDataFactory.createTwoNewAccountsForTest(
                originalCustomerId,
                originalAccountId,
                fromAccountType,
                toAccountType
        );
        BigDecimal expectedBalanceAfterTransfer = testData.account2OriginalBalance().add(transferAmount);
        TransactionDto expectedTransaction = new TransactionDto(
                0,
                testData.account2().id(),
                TransactionTypes.CREDIT,
                time.getUnixDateForApiAssertion(),
                transferAmount,
                TRANSFER_DESCRIPTION);
        List<TransactionDto> transactionsBeforeTransfer = accountActionsAPI.sendGetRequestForAllTransactionsForAccount(testData.account2().id());
        String expectedSuccessMessage = responsesFactory.buildSuccessfulTransferResponse(transferAmount, testData.account1().id(), testData.account2().id());

        //Act: Transfer funds between accounts, retrieve actual transfer dto and actual balance
        String actualSuccessMessage = accountActionsAPI.sendPostRequestToTransferFunds(
                testData.account1().id(),
                testData.account2().id(),
                transferAmount);
        TransactionDto actualTransaction = beHelper.determineNewTransactionFromList(transactionsBeforeTransfer, testData.account2().id());
        BigDecimal actualBalanceAfterTransfer = beHelper.retrieveAccountBalance(originalCustomerId, testData.account2().id());

        //Assert: Verify equality between expected and actual success message, transfer DTOs, and balances
        Assertions.assertEquals(expectedSuccessMessage, actualSuccessMessage);
        Assertions.assertEquals(testData.account2().id(), actualTransaction.accountId());
        Assertions.assertEquals(expectedTransaction.type(), actualTransaction.type());
        Assertions.assertEquals(expectedTransaction.date(), actualTransaction.date());
        Assertions.assertEquals(transferAmount, actualTransaction.amount());
        Assertions.assertEquals(TRANSFER_DESCRIPTION, actualTransaction.description());
        Assertions.assertEquals(expectedBalanceAfterTransfer, actualBalanceAfterTransfer);
    }

    //Method sources

    private static Stream<Arguments> provideTransferAmountAndAccountTypes() {
        return Stream.of(
                Arguments.of("small", new BigDecimal("20.00"), AccountTypes.CHECKING, AccountTypes.CHECKING),
                Arguments.of("small", new BigDecimal("20.00"), AccountTypes.CHECKING, AccountTypes.SAVINGS),
                Arguments.of("small", new BigDecimal("20.00"), AccountTypes.SAVINGS, AccountTypes.CHECKING),
                Arguments.of("small", new BigDecimal("20.00"), AccountTypes.SAVINGS, AccountTypes.SAVINGS),
                Arguments.of("medium", new BigDecimal("1000.00"), AccountTypes.CHECKING, AccountTypes.CHECKING),
                Arguments.of("medium", new BigDecimal("1000.00"), AccountTypes.CHECKING, AccountTypes.SAVINGS),
                Arguments.of("medium", new BigDecimal("1000.00"), AccountTypes.SAVINGS, AccountTypes.CHECKING),
                Arguments.of("medium", new BigDecimal("1000.00"), AccountTypes.SAVINGS, AccountTypes.SAVINGS),
                Arguments.of("large", new BigDecimal("500000.00"), AccountTypes.CHECKING, AccountTypes.CHECKING),
                Arguments.of("large", new BigDecimal("500000.00"), AccountTypes.CHECKING, AccountTypes.SAVINGS),
                Arguments.of("large", new BigDecimal("500000.00"), AccountTypes.SAVINGS, AccountTypes.CHECKING),
                Arguments.of("large", new BigDecimal("500000.00"), AccountTypes.SAVINGS, AccountTypes.SAVINGS)
        );
    }

}

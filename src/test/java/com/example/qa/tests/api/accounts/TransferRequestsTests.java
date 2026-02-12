package com.example.qa.tests.api.accounts;

import com.example.qa.api.clients.AccountActionsAPI;
import com.example.qa.api.dtos.AccountDto;
import com.example.qa.api.dtos.TransactionDto;
import com.example.qa.api.http.ResponsesFactory;
import com.example.qa.tests.base_tests.AuthenticatedBaseTest;
import com.example.qa.tests.utils.TimeUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import com.example.qa.enums.AccountTypes;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TransferRequestsTests extends AuthenticatedBaseTest {

    private final String TRANSFER_DESCRIPTION = "Funds Transfer Received";
    private final String TRANSFER_TYPE = "Credit";

    protected AccountActionsAPI accountActionsAPI;
    protected TimeUtils time;
    protected AccountDto originalCheckingAccount;
    protected ResponsesFactory responsesFactory;
    protected int originalAccountId;
    protected int originalCustomerId;


    @BeforeAll
    void initApiAndGetContext() {
        accountActionsAPI = new AccountActionsAPI(request);
        time = new TimeUtils();
        responsesFactory = new ResponsesFactory();
        originalCheckingAccount = customerContext.getOriginalAccount();
        originalCustomerId = customerContext.getCustomerId();
        originalAccountId = originalCheckingAccount.id();
    }

    @ParameterizedTest(name = "Successful transfer POST request from {1} account to {2} account")
    @MethodSource("provideTransferAmountAndAccountTypes")
    void fundsCanBeTransferredBetweenAccounts(BigDecimal transferAmount, AccountTypes fromAccountType, AccountTypes toAccountType) {
        int fromAccountId = accountActionsAPI.createNewAccount(
                originalCustomerId,
                fromAccountType,
                originalAccountId)
                .id();
        AccountDto toAccount = accountActionsAPI.createNewAccount(
                originalCustomerId,
                toAccountType,
                originalAccountId);
        int toAccountId = toAccount.id();
        BigDecimal expectedBalanceAfterTransfer = helper.retrieveAccountBalance(originalCustomerId, toAccountId).add(transferAmount);
        TransactionDto expectedTransaction = new TransactionDto(
                0,
                toAccountId,
                TRANSFER_TYPE,
                time.getUnixDateForApiAssertion(),
                transferAmount,
                TRANSFER_DESCRIPTION);
        List<TransactionDto> transactionsBeforeTransfer = accountActionsAPI.sendGetRequestForAllTransactionsForAccount(toAccountId);
        String expectedSuccessMessage = responsesFactory.buildSuccessfulTransferResponse(transferAmount, fromAccountId, toAccountId);

        String actualSuccessMessage = accountActionsAPI.sendPostRequestToTransferFunds(
                fromAccountId,
                toAccountId,
                transferAmount);
        Assertions.assertEquals(expectedSuccessMessage, actualSuccessMessage);
        TransactionDto actualTransaction = determineNewTransactionFromList(transactionsBeforeTransfer, toAccountId);
        BigDecimal actualBalanceAfterTransfer = helper.retrieveAccountBalance(originalCustomerId, toAccountId);

        Assertions.assertEquals(toAccountId, actualTransaction.accountId());
        Assertions.assertEquals(expectedTransaction.type(), actualTransaction.type());
        Assertions.assertEquals(expectedTransaction.date(), actualTransaction.date());
        Assertions.assertEquals(transferAmount, actualTransaction.amount());
        Assertions.assertEquals(TRANSFER_DESCRIPTION, actualTransaction.description());
        Assertions.assertEquals(expectedBalanceAfterTransfer, actualBalanceAfterTransfer);
    }

    @ParameterizedTest(name = "{0} of {1} transferred via API from new {2} account to original account appears in overview")
    @MethodSource("provideTransferAmountAndAccountTypes")
    void transferredFundsToOriginalAccountShouldAppearInOverview(BigDecimal transferAmount, AccountTypes fromAccountType) {
        AccountDto newAccountToSendFrom = accountActionsAPI.createNewAccount(
                customerContext.getCustomerId(),
                fromAccountType,
                originalCheckingAccount.id());

        accountActionsAPI.sendPostRequestToTransferFunds(
                newAccountToSendFrom.id(),
                originalCheckingAccount.id(),
                transferAmount);

        BigDecimal expectedBalanceAfterTransfer = helper.retrieveAccountBalance(originalCustomerId, originalAccountId);
        goToOverviewAndWaitForTableVisibility();
        accountsOverviewPage.assertThatBalanceIsVisibleAndAmountIsCorrect(originalCheckingAccount.id(), expectedBalanceAfterTransfer);
    }

    //Method sources

    private static Stream<Arguments> provideTransferAmountAndAccountTypes() {
        return Stream.of(
                Arguments.of(new BigDecimal("20.00"), AccountTypes.CHECKING, AccountTypes.CHECKING),
                Arguments.of(new BigDecimal("20.00"), AccountTypes.CHECKING, AccountTypes.SAVINGS),
                Arguments.of(new BigDecimal("20.00"), AccountTypes.SAVINGS, AccountTypes.CHECKING),
                Arguments.of(new BigDecimal("20.00"), AccountTypes.SAVINGS, AccountTypes.SAVINGS)
        );
    }

    //Test helpers

    public void goToOverviewAndWaitForTableVisibility() {
        goTo.accountsOverview();
        assertThat(accountsOverviewPage.accountTable()).isVisible();
    }

    public TransactionDto determineNewTransactionFromList(List<TransactionDto> beforeTransfer, int accountId) {
        List<TransactionDto> transactionsAfterTransfer = accountActionsAPI.sendGetRequestForAllTransactionsForAccount(accountId);
        List<TransactionDto> newTransactions = transactionsAfterTransfer.stream().filter(a -> !beforeTransfer.contains(a)).toList();
        Assertions.assertEquals(1, newTransactions.size());
        return newTransactions.get(0);
    }

}

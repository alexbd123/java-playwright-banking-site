package com.example.qa.tests.api.loans;

import com.example.qa.api.clients.AccountActionsAPI;
import com.example.qa.api.clients.ApplicationParametersAPI;
import com.example.qa.api.clients.LoansAPI;
import com.example.qa.api.dtos.AccountDto;
import com.example.qa.api.dtos.LoanResponseDto;
import com.example.qa.api.http.ResponsesFactory;
import com.example.qa.enums.AccountTypes;
import com.example.qa.enums.LoanProcessorParameters;
import com.example.qa.tests.base_tests.AuthenticatedBaseTest;
import com.example.qa.tests.test_data.test_data_factories.AccountsDataFactory;
import com.example.qa.tests.utils.TimeUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AvailableFundsLoansTests extends AuthenticatedBaseTest {

    protected LoansAPI loansRequests;
    protected AccountActionsAPI accountsRequests;
    protected AccountDto originalCheckingAccount;
    protected TimeUtils time;
    protected ResponsesFactory responsesFactory;
    protected AccountsDataFactory accountsDataFactory;
    protected int originalAccountId;
    protected int originalCustomerId;
    protected ApplicationParametersAPI applicationParametersAPI;

    @BeforeAll
    void initAPIAndSetLoanParameterToDownPayment() {
        accountsRequests = new AccountActionsAPI(request);
        time = new TimeUtils();
        responsesFactory = new ResponsesFactory();
        originalCheckingAccount = customerContext.originalAccount();
        originalCustomerId = customerContext.customerId();
        originalAccountId = originalCheckingAccount.id();
        accountsDataFactory = new AccountsDataFactory(request);
        loansRequests = new LoansAPI(request);
        applicationParametersAPI = new ApplicationParametersAPI(request);
        applicationParametersAPI.setLoanApprovalType(LoanProcessorParameters.AVAILABLE_FUNDS);
    }

    @ParameterizedTest(name = "Loan request when available funds = {0}% of loan amount should be {1}")
    @MethodSource("loanPercentageExpectedResult")
    void correctLoanResultForAvailableFunds(BigDecimal percentage, String approvalStatus) {
        BigDecimal loanAmount = determineLoanAmount(originalCustomerId, originalAccountId, percentage);

        LoanResponseDto actualResponse = loansRequests.requestLoan(
                originalCustomerId,
                loanAmount,
                BigDecimal.ZERO,
                originalAccountId
        );
        Assertions.assertEquals(Objects.equals(approvalStatus, "approved"), actualResponse.approved());
    }

    @ParameterizedTest(name = "Loan request with available funds of {0}% of loan amount creates loan account with correct balance")
    @MethodSource("validLoanPercentages")
    void loanAccountCreatedAfterSuccessfulLoanRequest(BigDecimal percentage) {
        BigDecimal loanAmount = determineLoanAmount(originalCustomerId, originalAccountId, percentage);
        List<AccountDto> accountsBeforeRequest = accountsRequests.sendGetRequestForCustomerAccountsInfo(originalCustomerId);
        LoanResponseDto loan = loansRequests.requestLoan(
                originalCustomerId,
                loanAmount,
                BigDecimal.ZERO,
                originalAccountId
        );
        Assertions.assertTrue(loan.approved());
        AccountDto expectedLoanAccount = new AccountDto(
                loan.accountId(),
                originalCustomerId,
                AccountTypes.LOAN,
                loanAmount
        );
        AccountDto actualLoanAccount = beHelper.determineNewAccountFromList(accountsBeforeRequest, originalCustomerId);
        Assertions.assertEquals(expectedLoanAccount, actualLoanAccount);
    }

    public BigDecimal determineLoanAmount(int customerId, int accountId, BigDecimal percentage) {
        BigDecimal availableFunds = beHelper.retrieveAccountBalance(customerId, accountId);
        return availableFunds
                .multiply(new BigDecimal("100"))
                .divide(percentage, 10, RoundingMode.HALF_UP)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private static Stream<Arguments> loanPercentageExpectedResult() {
        return Stream.of(
                Arguments.of(new BigDecimal("1"), "denied"),
                Arguments.of(new BigDecimal("10"), "denied"),
                Arguments.of(new BigDecimal("15.50"), "denied"),
                Arguments.of(new BigDecimal("19.99"), "denied"),
                Arguments.of(new BigDecimal("20"), "approved"),
                Arguments.of(new BigDecimal("20.01"), "approved"),
                Arguments.of(new BigDecimal("21"), "approved"),
                Arguments.of(new BigDecimal("30"), "approved")
        );
    }

    private static Stream<Arguments> validLoanPercentages() {
        return Stream.of(
                Arguments.of(new BigDecimal("20")),
                Arguments.of(new BigDecimal("30")),
                Arguments.of(new BigDecimal("40")),
                Arguments.of(new BigDecimal("50")),
                Arguments.of(new BigDecimal("60")),
                Arguments.of(new BigDecimal("70")),
                Arguments.of(new BigDecimal("80")),
                Arguments.of(new BigDecimal("90"))
        );
    }


    //Test positive and negative loan approval using all three loan processing criteria
    //Implement method to change loan approval parameters via API
    //Test correct Debit transaction on loan approval
    //Test loan account creation on loan approval

}


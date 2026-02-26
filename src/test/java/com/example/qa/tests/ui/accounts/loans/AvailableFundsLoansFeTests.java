package com.example.qa.tests.ui.accounts.loans;

import com.example.qa.api.clients.ApplicationParametersAPI;
import com.example.qa.api.clients.LoansAPI;
import com.example.qa.api.dtos.LoanResponseDto;
import com.example.qa.api.helpers.ApiHelper;
import com.example.qa.enums.*;
import com.example.qa.pages.ui_dtos.AccountActivityDto;
import com.example.qa.pages.ui_dtos.FeLoanResponseDto;
import com.example.qa.tests.base_tests.AuthenticatedBaseTest;
import com.example.qa.tests.utils.TimeUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

public class AvailableFundsLoansFeTests extends AuthenticatedBaseTest {

    protected ApiHelper beHelper;
    protected int originalAccountId;
    protected int originalCustomerId;
    protected TimeUtils time;
    protected ApplicationParametersAPI applicationParametersAPI;
    protected LoansAPI loansAPI;


    @BeforeAll
    void initApiAndGetContext() {
        beHelper = new ApiHelper(request);
        originalAccountId = customerContext.originalAccount().id();
        originalCustomerId = customerContext.customerId();
        time = new TimeUtils();
        applicationParametersAPI = new ApplicationParametersAPI(request);
        loansAPI = new LoansAPI(request);
    }

    @BeforeEach
    void setLoanParameterToAvailableFunds() {
        applicationParametersAPI.setLoanApprovalType(LoanProcessorParameter.AVAILABLE_FUNDS);
    }

    @ParameterizedTest(name = "Available funds equal to {0}% of loan amount -> loan {1}")
    @MethodSource("availableFundsPercentageAndApprovalStatus")
    void correctLoanApprovalStatusForAvailableFunds(BigDecimal percentage, LoanApprovalStatus approvalStatus) {
        BigDecimal loanAmount = beHelper.determineAvailableFundsLoanAmount(originalCustomerId, percentage);
        boolean approved = approvalStatus.equals(LoanApprovalStatus.APPROVED);
        FeLoanResponseDto expectedLoanStatus = new FeLoanResponseDto(
                LoanProvider.WSDL.getProvider(),
                time.getDateInUiFormat(),
                approvalStatus.getStatus(),
                UILoanApprovalMessage.insufficientFundsFromBoolean(approved).getMessage()
        );

        goTo.requestLoan();
        requestLoansPage.applyForLoan(loanAmount, BigDecimal.ZERO, originalAccountId);
        FeLoanResponseDto actualLoanStatus = requestLoansPage.availableFundsToDto(approved);

        Assertions.assertEquals(expectedLoanStatus, actualLoanStatus);
    }

    @ParameterizedTest(name = "Valid loan request when funds = {0}% of loan amount -> loan account created and visible in overview")
    @MethodSource("validLoanPercentages")
    void loanAccountVisibleWhenLoanSuccessful(BigDecimal percentage) {
        BigDecimal loanAmount = beHelper.determineAvailableFundsLoanAmount(originalCustomerId, percentage);
        LoanResponseDto loanResponse = loansAPI.requestLoan(originalCustomerId, loanAmount, BigDecimal.ZERO, originalAccountId);
        AccountActivityDto expectedLoanAccount = new AccountActivityDto(
                loanResponse.accountId(),
                AccountType.LOAN,
                loanAmount,
                loanAmount
        );
        feHelper.goToOverviewAndWaitForTableVisibility();
        accountsOverviewPage.assertBalanceVisibility(loanResponse.accountId(), loanAmount);
        accountsOverviewPage.goToAccountActivity(loanResponse.accountId());
        AccountActivityDto actualLoanAccount = accountActivityPage.toDto();

        Assertions.assertEquals(expectedLoanAccount, actualLoanAccount);
    }

    private static Stream<Arguments> availableFundsPercentageAndApprovalStatus() {
        return Stream.of(
                Arguments.of(new BigDecimal("10"),LoanApprovalStatus.DENIED),
                Arguments.of(new BigDecimal("15"), LoanApprovalStatus.DENIED),
                Arguments.of(new BigDecimal("19.9"), LoanApprovalStatus.DENIED),
                Arguments.of(new BigDecimal("20"), LoanApprovalStatus.APPROVED),
                Arguments.of(new BigDecimal("20.01"), LoanApprovalStatus.APPROVED),
                Arguments.of(new BigDecimal("25"), LoanApprovalStatus.APPROVED)
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

}

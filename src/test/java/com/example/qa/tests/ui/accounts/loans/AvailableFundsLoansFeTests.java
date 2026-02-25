package com.example.qa.tests.ui.accounts.loans;

import com.example.qa.api.clients.ApplicationParametersAPI;
import com.example.qa.api.helpers.ApiHelper;
import com.example.qa.enums.LoanApprovalStatus;
import com.example.qa.enums.LoanProcessorParameter;
import com.example.qa.enums.LoanProvider;
import com.example.qa.enums.UILoanApprovalMessage;
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


    @BeforeAll
    void initApiAndGetContext() {
        beHelper = new ApiHelper(request);
        originalAccountId = customerContext.originalAccount().id();
        originalCustomerId = customerContext.customerId();
        time = new TimeUtils();
        applicationParametersAPI = new ApplicationParametersAPI(request);
    }

    @BeforeEach
    void setLoanParameterToAvailableFunds() {
        applicationParametersAPI.setLoanApprovalType(LoanProcessorParameter.AVAILABLE_FUNDS);
    }

    @ParameterizedTest(name = "Available funds equal to {0}% of loan amount means loan is {2}")
    @MethodSource("availableFundsPercentageAndApprovalStatus")
    void correctLoanApprovalStatusForAvailableFunds(BigDecimal percentage, boolean approved, LoanApprovalStatus approvalStatus) {
        BigDecimal loanAmount = beHelper.determineAvailableFundsLoanAmount(originalCustomerId, percentage);
        FeLoanResponseDto expectedLoanStatus = new FeLoanResponseDto(
                LoanProvider.WSDL.getProvider(),
                time.getDateInUiFormat(),
                LoanApprovalStatus.fromBoolean(approved).getStatus(),
                UILoanApprovalMessage.insufficientFundsFromBoolean(approved).getMessage()
        );

        goTo.requestLoan();
        requestLoansPage.applyForLoan(loanAmount, BigDecimal.ZERO, originalAccountId);
        FeLoanResponseDto actualLoanStatus = requestLoansPage.toDto(approved);

        Assertions.assertEquals(expectedLoanStatus, actualLoanStatus);
    }

    private static Stream<Arguments> availableFundsPercentageAndApprovalStatus() {
        return Stream.of(
                Arguments.of(new BigDecimal("10"), false, LoanApprovalStatus.DENIED),
                Arguments.of(new BigDecimal("15"), false, LoanApprovalStatus.DENIED),
                Arguments.of(new BigDecimal("19.9"), false, LoanApprovalStatus.DENIED),
                Arguments.of(new BigDecimal("20"), true, LoanApprovalStatus.APPROVED),
                Arguments.of(new BigDecimal("20.01"), true, LoanApprovalStatus.APPROVED),
                Arguments.of(new BigDecimal("25"), true, LoanApprovalStatus.APPROVED)
        );
    }

}

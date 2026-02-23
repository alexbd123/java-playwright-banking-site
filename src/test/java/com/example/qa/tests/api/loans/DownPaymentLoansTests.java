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
import com.example.qa.tests.test_data.test_data_records.NewAccountsForTests;
import com.example.qa.tests.utils.TimeUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.stream.Stream;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DownPaymentLoansTests extends AuthenticatedBaseTest {

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
        applicationParametersAPI.setLoanApprovalType(LoanProcessorParameters.DOWN_PAYMENT);
    }

    @ParameterizedTest(name = "Request for loan of {0} with down payment of {1} should be {2}")
    @MethodSource("provideLoadDownPaymentExpectedResult")
    void correctLoanResultForDownPayment(String loan, String downPayment, String approvalStatus) {
        NewAccountsForTests testData = accountsDataFactory.createOneNewAccountForTest(originalCustomerId, originalAccountId, AccountTypes.CHECKING);
        LoanResponseDto actualResponse = loansRequests.requestLoan(
                originalCustomerId,
                new BigDecimal(loan),
                new BigDecimal(downPayment),
                testData.account1().id()
        );
        boolean expectedApprovalStatus = Objects.equals(approvalStatus, "approved");
        Assertions.assertEquals(expectedApprovalStatus, actualResponse.approved());
    }

    private static Stream<Arguments> provideLoadDownPaymentExpectedResult() {
        return Stream.of(
                Arguments.of("500", "99.99", "denied"),
                Arguments.of("500", "100.00", "approved"),
                Arguments.of("500", "100.01", "approved"),
                Arguments.of("1000", "199.99", "denied"),
                Arguments.of("1000", "200.00", "approved"),
                Arguments.of("1000", "200.01", "approved"),
                Arguments.of("2000", "399.99", "denied"),
                Arguments.of("2000", "400.00", "approved"),
                Arguments.of("2000", "400.01", "approved")
        );
    }

    //Test positive and negative loan approval using all three loan processing criteria
    //Implement method to change loan approval parameters via API
    //Test correct Debit transaction on loan approval
    //Test loan account creation on loan approval

}

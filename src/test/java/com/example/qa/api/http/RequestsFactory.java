package com.example.qa.api.http;

import com.example.qa.api.dtos.User;
import com.example.qa.enums.AccountTypes;
import com.example.qa.enums.LoanProcessorParameters;

import java.math.BigDecimal;

public class RequestsFactory {

    //Accounts
    public String buildCreateNewAccountRequest(int customerId, AccountTypes accountType, int fromAccountId) {
        int accountTypeInt = (accountType == AccountTypes.CHECKING) ? 0 : 1;
        return String.format("?customerId=%d&newAccountType=%d&fromAccountId=%d",
                customerId,
                accountTypeInt,
                fromAccountId);
    }

    public String buildRetrieveCustomerAccountsRequest(int customerId) {
        return String.format("/%s/accounts", customerId);
    }

    public String buildInitialBalanceRequest(BigDecimal initialBalance) {
        return String.format(
                "/initialBalance/%.2f",
                initialBalance
        );
    }

    //Account actions
    public String buildWithdrawFundsRequest(int accountId, BigDecimal amount) {
        return String.format("?accountId=%d&amount=%.2f", accountId, amount);
    }

    public String buildTransferFundsRequests(int fromAccountId, int toAccountId, BigDecimal amount) {
        return String.format("?fromAccountId=%d&toAccountId=%d&amount=%.2f", fromAccountId, toAccountId, amount);
    }

    //Customers
    public String buildUpdateCustomerRequest(int customerId, User user) {
        return String.format(
                "/%d?firstName=%s&lastName=%s&street=%s&city=%s&state=%s&zipCode=%s&phoneNumber=%s&ssn=%s&username=%s&password=%s",
                customerId,
                user.firstName(),
                user.lastName(),
                user.address(),
                user.city(),
                user.state(),
                user.zipCode(),
                user.phoneNumber(),
                user.ssn(),
                user.username(),
                user.password()
        );
    }

    //Loans

    public String buildLoanRequest(int customerId, BigDecimal amount, BigDecimal downPayment, int fromAccountId) {
        return String.format(
                "?customerId=%d&amount=%.2f&downPayment=%.2f&fromAccountId=%d",
                customerId,
                amount,
                downPayment,
                fromAccountId
        );
    }

    public String buildLoanProcessorRequest(LoanProcessorParameters loanParameter) {
        return String.format(
                "/loanProcessor/%s",
                loanParameter.type
        );
    }

    public String buildLoanThresholdRequest(int percentage) {
        return String.format(
                "/loanProcessorThreshold/%d",
                percentage
        );
    }

}

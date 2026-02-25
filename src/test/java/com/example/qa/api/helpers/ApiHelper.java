package com.example.qa.api.helpers;

import com.example.qa.api.clients.AccountActionsAPI;
import com.example.qa.api.clients.CustomerAPI;
import com.example.qa.api.dtos.AccountDto;
import com.example.qa.api.dtos.CustomerDto;
import com.example.qa.api.dtos.TransactionDto;
import com.example.qa.api.dtos.User;
import com.example.qa.enums.AccountType;
import com.microsoft.playwright.APIRequestContext;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class ApiHelper {

    private final AccountActionsAPI accountActionsAPI;
    private final CustomerAPI customerAPI;

    public ApiHelper(APIRequestContext request) {
        this.accountActionsAPI = new AccountActionsAPI(request);
        this.customerAPI = new CustomerAPI(request);
    }

    public BigDecimal retrieveAccountBalance(int customerId, int accountId) {
        return accountActionsAPI.sendGetRequestForCustomerAccountsInfo(
                        customerId).stream()
                .filter(a -> a.id() == accountId)
                .map(AccountDto::balance)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Account not found"));
    }

    public User extractUserFromResponse(int customerId, String username, String password) {
        CustomerDto customer = customerAPI.sendGetRequestForCustomerDetails(customerId);
        return new User(
                customer.firstName(),
                customer.lastName(),
                customer.address().street(),
                customer.address().city(),
                customer.address().state(),
                customer.address().zipCode(),
                customer.phoneNumber(),
                customer.ssn(),
                username,
                password
                );
    }

    public TransactionDto determineNewTransactionFromList(List<TransactionDto> beforeTransfer, int accountId) {
        List<TransactionDto> transactionsAfterTransfer = accountActionsAPI.sendGetRequestForAllTransactionsForAccount(accountId);
        List<TransactionDto> newTransactions = transactionsAfterTransfer.stream().filter(a -> !beforeTransfer.contains(a)).toList();
        if (newTransactions.size() == 1) {
            return newTransactions.get(0);
        } else if (newTransactions.isEmpty()) {
            throw new RuntimeException("There are no newly created transactions for account " + accountId);
        }
        throw new RuntimeException (
                "More than one new transaction found for account " + accountId
        );
    }

    public AccountDto determineNewAccountFromList(List<AccountDto> accountsBefore, int customerId) {
        List<AccountDto> accountsAfter = accountActionsAPI.sendGetRequestForCustomerAccountsInfo(customerId);
        List<AccountDto> newAccounts = accountsAfter.stream().filter(a -> !accountsBefore.contains(a)).toList();
        if (newAccounts.size() == 1) {
            return newAccounts.get(0);
        } else if (newAccounts.isEmpty()) {
            throw new RuntimeException("There are no newly created accounts for customer " + customerId);
        }
        throw new RuntimeException (
                "More than one new account found for account " + customerId
        );
    }

    public BigDecimal getTotalAvailableFundsForLoan(int customerId) {
        List<AccountDto> accounts = accountActionsAPI.sendGetRequestForCustomerAccountsInfo(customerId);
        BigDecimal availableFunds = new BigDecimal("0");
        for (AccountDto account : accounts) {
            if (account.type() != AccountType.LOAN) {
                availableFunds = availableFunds.add(retrieveAccountBalance(customerId, account.id()));
            }
        }
        return availableFunds;
    }

    public BigDecimal determineAvailableFundsLoanAmount(int customerId, BigDecimal percentage) {
        BigDecimal availableFunds = getTotalAvailableFundsForLoan(customerId);
        return availableFunds
                .multiply(new BigDecimal("100"))
                .divide(percentage, 10, RoundingMode.HALF_UP)
                .setScale(2, RoundingMode.HALF_UP);
    }

}
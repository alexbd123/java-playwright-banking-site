package com.example.qa.tests.test_data;

import com.example.qa.api.dtos.TransactionDto;

public record WithdrawalTransactionData(int accountId, TransactionDto expectedTransaction, String transactionDate) {
}

package com.example.qa.tests.test_data.test_data_records;

import com.example.qa.api.dtos.TransactionDto;

public record WithdrawalTransactionData(
        int accountId,
        TransactionDto expectedTransaction,
        String uiFormattedTransactionDate
) {
}

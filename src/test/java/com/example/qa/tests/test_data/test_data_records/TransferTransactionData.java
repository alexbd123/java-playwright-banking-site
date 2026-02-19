package com.example.qa.tests.test_data.test_data_records;

import com.example.qa.api.dtos.TransactionDto;

import java.math.BigDecimal;

public record TransferTransactionData(
        int accountId,
        BigDecimal originalBalance,
        TransactionDto transaction,
        String transactionDate
) {
}

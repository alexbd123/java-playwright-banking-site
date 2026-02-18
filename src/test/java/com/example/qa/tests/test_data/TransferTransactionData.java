package com.example.qa.tests.test_data;

import com.example.qa.api.dtos.TransactionDto;

import java.math.BigDecimal;

public record TransferTransactionData(int accountId, BigDecimal originalBalance, TransactionDto expectedTransaction) {
}

package com.example.qa.tests.test_data.test_data_records;

import com.example.qa.api.dtos.AccountDto;

import java.math.BigDecimal;

public record NewAccountsForTests(
        AccountDto account1,
        BigDecimal account1OriginalBalance,
        AccountDto account2,
        BigDecimal account2OriginalBalance
) {
}

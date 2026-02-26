package com.example.qa.pages.ui_dtos;

import com.example.qa.enums.AccountType;

import java.math.BigDecimal;

public record AccountActivityDto(

        int accountId,

        AccountType accountType,

        BigDecimal balance,

        BigDecimal availableBalance

)
{}

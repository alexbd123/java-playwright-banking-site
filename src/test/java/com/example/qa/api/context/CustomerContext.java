package com.example.qa.api.context;

import com.example.qa.api.dtos.AccountDto;

public record CustomerContext(int customerId, AccountDto originalAccount) {

}

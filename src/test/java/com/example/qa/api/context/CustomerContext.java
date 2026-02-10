package com.example.qa.api.context;

import com.example.qa.api.dtos.AccountDto;

public class CustomerContext {

    private final int customerId;
    private final AccountDto originalAccount;
    
    CustomerContext (int customerId, AccountDto originalAccount) {
        this.customerId = customerId;
        this.originalAccount = originalAccount;
    }
    
    public int getCustomerId() {
        return customerId;
    }
    
    public AccountDto getOriginalAccount() {
        return originalAccount;
    }

}

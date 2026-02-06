package com.example.qa.api.context;

public class CustomerContext {

    private final int customerId;
    private final int originalAccountId;
    
    CustomerContext (int customerId, int originalAccountId) {
        this.customerId = customerId;
        this.originalAccountId = originalAccountId;
    }
    
    public int getCustomerId() {
        return customerId;
    }
    
    public int getOriginalAccountId() {
        return originalAccountId;
    }

}

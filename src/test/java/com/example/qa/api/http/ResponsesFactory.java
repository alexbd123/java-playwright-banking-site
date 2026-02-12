package com.example.qa.api.http;

import java.math.BigDecimal;

public class ResponsesFactory {

    public String buildSuccessfulTransferResponse(BigDecimal transferAmount, int fromAccountId, int toAccountId) {
        return String.format(
                "Successfully transferred $%.2f from account #%d to account #%d",
                transferAmount,
                fromAccountId,
                toAccountId
        );
    }
}

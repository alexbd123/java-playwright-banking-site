package com.example.qa.api.clients;

import com.example.qa.api.http.HTTPRequests;
import com.example.qa.api.http.RequestsFactory;
import com.example.qa.enums.LoanProcessorParameters;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;

import java.math.BigDecimal;

public class ApplicationParametersAPI {

    private final HTTPRequests http;
    private final RequestsFactory requestsFactory;

    public ApplicationParametersAPI(APIRequestContext request) {
        this.http = new HTTPRequests(request);
        this.requestsFactory = new RequestsFactory();
    }

    public void setLoanApprovalType(LoanProcessorParameters parameter) {
        APIResponse response = http.post(
                "setParameter",
                requestsFactory.buildLoanProcessorRequest(parameter)
        );
        if (response.status() != 204) {
            throw new RuntimeException("Failed : HTTP error code : " + response.status());
        }
    }

    public void setLoanThreshold(int percentage) {
        APIResponse response = http.post(
                "setParameter",
                requestsFactory.buildLoanThresholdRequest(percentage)
        );
    }

    public void setInitialBalance(BigDecimal initialBalance) {
        APIResponse response = http.post(
                "setParameter",
                requestsFactory.buildInitialBalanceRequest(initialBalance)
        );
    }

}

package com.example.qa.api.clients;

import com.example.qa.api.dtos.LoanResponseDto;
import com.example.qa.api.http.HTTPRequests;
import com.example.qa.api.http.RequestsFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;

import java.math.BigDecimal;

public class LoansAPI {

    private final HTTPRequests http;
    private final RequestsFactory requestsFactory;
    ObjectMapper mapper = new ObjectMapper();

    public LoansAPI(APIRequestContext request) {
        http = new HTTPRequests(request);
        requestsFactory = new RequestsFactory();
    }

    public LoanResponseDto requestLoan(
            int customerId,
            BigDecimal loanAmount,
            BigDecimal downPayment,
            int fromAccountId
    ) {
        APIResponse response = http.post(
                "requestLoan", requestsFactory.buildLoanRequest(
                        customerId,
                        loanAmount,
                        downPayment,
                        fromAccountId
                )
        );
        if (response.ok()) {
            try {
                return mapper.readValue(response.text(), LoanResponseDto.class);
            } catch (Exception e) {
                throw new RuntimeException("Failed to parse loan request response", e);
            }
        } else if (response.status() == 400) {
            try {
                return mapper.readValue(response.text(), LoanResponseDto.class);
            } catch (Exception e) {
                throw new RuntimeException("Failed to parse loan request response", e);
            }
        } else {
            throw new RuntimeException("Failed to send loan request: " + response.text());
        }
    }

}

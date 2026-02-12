package com.example.qa.api.http;

import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;

public class HTTPRequests {
    private final APIRequestContext request;

    public HTTPRequests(APIRequestContext request) {this.request = request;}

    public APIResponse post(String endpoint, String requestString) {
        return request.post(endpoint + requestString);
    }

    public APIResponse get(String endpoint, String requestString) {
        return request.get(endpoint + requestString);
    }
}

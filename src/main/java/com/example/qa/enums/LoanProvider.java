package com.example.qa.enums;

public enum LoanProvider {

    WSDL("Wealth Securities Dynamic Loans (WSDL)");

    public final String provider;

    LoanProvider(String provider) {
        this.provider = provider;
    }

    public String getProvider() {
        return provider;
    }

}

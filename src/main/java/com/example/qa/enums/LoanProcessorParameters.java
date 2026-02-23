package com.example.qa.enums;

public enum LoanProcessorParameters {

    AVAILABLE_FUNDS("funds"),
    DOWN_PAYMENT("down"),
    COMBINED("combined");

    public final String type;

    LoanProcessorParameters(String type) {
        this.type = type;
    }

}

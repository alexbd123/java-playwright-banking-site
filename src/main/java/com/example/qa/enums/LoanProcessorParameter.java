package com.example.qa.enums;

public enum LoanProcessorParameter {

    AVAILABLE_FUNDS("funds"),
    DOWN_PAYMENT("down"),
    COMBINED("combined");

    public final String type;

    LoanProcessorParameter(String type) {
        this.type = type;
    }

}

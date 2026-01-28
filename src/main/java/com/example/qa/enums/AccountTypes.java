package com.example.qa.enums;


public enum AccountTypes {
    CHECKING("CHECKING"),
    SAVINGS("SAVINGS");

    public final String label;

    AccountTypes(String label) {
        this.label = label;
    }
    public String getLabel() { return label; }
}


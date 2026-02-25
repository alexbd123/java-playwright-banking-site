package com.example.qa.enums;


public enum AccountType {
    CHECKING("0", "CHECKING"),
    SAVINGS("1", "SAVINGS"),
    LOAN("2", "LOAN");

    public final String value;
    public final String label;

    AccountType(String value, String label) {
        this.value = value;
        this.label = label;
    }
    public String getValue() { return value; }
    public String getLabel() { return label; }
}


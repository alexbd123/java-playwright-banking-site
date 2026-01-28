package com.example.qa.enums;


public enum AccountTypes {
    CHECKING("0", "CHECKING"),
    SAVINGS("1", "SAVINGS");

    public final String value;
    public final String label;

    AccountTypes(String value, String label) {
        this.value = value;
        this.label = label;
    }
    public String value() { return value; }
    public String label() { return label; }
}


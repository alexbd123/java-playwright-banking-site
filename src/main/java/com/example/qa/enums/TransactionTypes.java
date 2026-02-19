package com.example.qa.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TransactionTypes {
    DEBIT("Debit"),
    CREDIT("Credit");

    private final String label;

    TransactionTypes(String label) { this.label = label; }

    @JsonValue
    public String getLabel() { return label; }

    @Override
    public String toString() { return label; }

    @JsonCreator
    public static TransactionTypes fromJson(String value) {
        return fromString(value);
    }

    public static TransactionTypes fromString(String value) {
        if (value == null) throw new IllegalArgumentException("Null type");
        String normal = value.trim();
        for (TransactionTypes t : values()) {
            if (t.label.equalsIgnoreCase(normal) || t.name().equalsIgnoreCase(normal)) {
                return t;
            }
        }
        throw new IllegalArgumentException("Unknown transaction type: " + value);
    }
}
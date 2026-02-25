package com.example.qa.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TransactionType {
    DEBIT("Debit"),
    CREDIT("Credit");

    private final String label;

    TransactionType(String label) { this.label = label; }

    @JsonValue
    public String getLabel() { return label; }

    @Override
    public String toString() { return label; }

    @JsonCreator
    public static TransactionType fromJson(String value) {
        return fromString(value);
    }

    public static TransactionType fromString(String value) {
        if (value == null) throw new IllegalArgumentException("Null type");
        String normal = value.trim();
        for (TransactionType t : values()) {
            if (t.label.equalsIgnoreCase(normal) || t.name().equalsIgnoreCase(normal)) {
                return t;
            }
        }
        throw new IllegalArgumentException("Unknown transaction type: " + value);
    }
}
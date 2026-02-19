package com.example.qa.enums;

public enum RegistrationField {
    FIRST_NAME("First name is required."),
    LAST_NAME("Last name is required."),
    ADDRESS("Address is required."),
    CITY("City is required."),
    STATE("State is required."),
    ZIP_CODE("Zip Code is required."),
    PHONE("Phone is required."),
    SSN("Social Security Number is required."),
    USERNAME("Username is required."),
    PASSWORD("Password is required."),
    CONFIRM_PASSWORD("Password confirmation is required.");

    public final String errorMessage;

    RegistrationField(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    public String getErrorMessage() {return errorMessage;}
}
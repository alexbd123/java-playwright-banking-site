package com.example.qa.enums;

public enum RegistrationFieldErrorMessage {
    FIRST_NAME_REQUIRED("First name", "First name is required."),
    LAST_NAME_REQUIRED("Last name", "Last name is required."),
    ADDRESS_REQUIRED("Address", "Address is required."),
    CITY_REQUIRED("City", "City is required."),
    STATE_REQUIRED("State", "State is required."),
    ZIP_CODE_REQUIRED("Zip code", "Zip Code is required."),
    SSN_REQUIRED("SSN", "Social Security Number is required."),
    USERNAME_REQUIRED("Username", "Username is required."),
    PASSWORD_REQUIRED("Password", "Password is required."),
    CONFIRM_PASSWORD_REQUIRED("Confirm password", "Password confirmation is required.");
    
    public final String field;
    public final String errorMessage;

    RegistrationFieldErrorMessage(String field, String errorMessage) {
        this.field = field;
        this.errorMessage = errorMessage;
    }
    public String getField() {return field;}
    public String getErrorMessage() {return errorMessage;}
}

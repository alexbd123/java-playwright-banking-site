package com.example.qa.enums;

public enum UILoanApprovalMessage {

    INSUFFICIENT_AVAILABLE_FUNDS("We cannot grant a loan in that amount with your available funds."),
    LOAN_APPROVED("Congratulations, your loan has been approved.");

    public final String message;

    UILoanApprovalMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public static UILoanApprovalMessage insufficientFundsFromBoolean(boolean approved) {
        return approved ? LOAN_APPROVED : INSUFFICIENT_AVAILABLE_FUNDS;
    }

}

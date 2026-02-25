package com.example.qa.enums;

public enum LoanApprovalStatus {

    APPROVED("Approved"),
    DENIED("Denied");

    public final String status;

    LoanApprovalStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public static LoanApprovalStatus fromBoolean(boolean approved) {
        return approved ? APPROVED : DENIED;
    }
}

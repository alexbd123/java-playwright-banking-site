package com.example.qa.pages.ui_dtos;

public record FeLoanResponseDto(

        String loanProvider,

        String date,

        String approvalStatus,

        String loanApprovalMessage

) {}

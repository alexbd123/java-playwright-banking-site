package com.example.qa.pages;

import com.example.qa.api.dtos.TransactionDto;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class TransactionDetailsPage {
    private final Page page;
    private final Locator transactionIdCell;
    private final Locator dateCell;
    private final Locator descriptionCell;
    private final Locator typeCell;
    private final Locator amountCell;

    private static final DateTimeFormatter UI_DATE = DateTimeFormatter.ofPattern("MM-dd-yyyy");

    public TransactionDetailsPage(Page page) {
        this.page = page;
        this.transactionIdCell = page.locator("//td[.//b[normalize-space()='Transaction ID:']]/following-sibling::td[1]");
        this.dateCell = page.locator("//td[.//b[normalize-space()='Date:']]/following-sibling::td[1]");
        this.descriptionCell = page.locator("//td[.//b[normalize-space()='Description:']]/following-sibling::td[1]");
        this.typeCell = page.locator("//td[.//b[normalize-space()='Type:']]/following-sibling::td[1]");
        this.amountCell = page.locator("//td[.//b[normalize-space()='Amount:']]/following-sibling::td[1]");
    }

    public String getTransactionId() {
        return transactionIdCell.innerText();
    }

    public String getDate() {
        return dateCell.innerText();
    }

    public String getDescription() {
        return descriptionCell.innerText();
    }

    public String getType() {
        return typeCell.innerText();
    }


    public BigDecimal getAmount() {
        String raw = amountCell.innerText().trim();
        String cleaned = raw.replaceAll("[^0-9.\\-]", "");
        return new BigDecimal(cleaned);
    }


    public TransactionDto toDto(int accountId) {
        return new TransactionDto(
            Integer.parseInt(getTransactionId()),
            accountId,
            getType(),
            convertTransactionDateToUnix(getDate()),
            getAmount(),
            getDescription()
    );
    }

    public String convertTransactionDateToUnix(String date) {
        LocalDate dateFormated = LocalDate.parse(date, UI_DATE);

        return String.valueOf(dateFormated
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli());
    }
}

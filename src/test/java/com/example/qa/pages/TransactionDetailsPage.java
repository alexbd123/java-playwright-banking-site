package com.example.qa.pages;

import com.example.qa.api.dtos.TransactionDto;
import com.example.qa.enums.TransactionType;
import com.example.qa.tests.utils.TimeUtils;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

import java.math.BigDecimal;

public class TransactionDetailsPage {
    private final Page page;
    private final TimeUtils time;
    private final Locator transactionIdCell;
    private final Locator dateCell;
    private final Locator descriptionCell;
    private final Locator typeCell;
    private final Locator amountCell;

    public TransactionDetailsPage(Page page) {
        this.page = page;
        this.time = new TimeUtils();
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

    public TransactionType getType() {
        String text = typeCell.innerText().trim();
        return TransactionType.fromString(text);
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
            time.convertUIDateToUnix(getDate()),
            getAmount(),
            getDescription()
    );
    }

}

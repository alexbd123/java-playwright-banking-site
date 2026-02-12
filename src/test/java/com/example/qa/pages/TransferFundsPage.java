package com.example.qa.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

import java.math.BigDecimal;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class TransferFundsPage {
    private Page page;
    private final Locator amountInput;
    private final Locator fromAccountSelect;
    private final Locator toAccountSelect;
    private final Locator transferButton;
    private final Locator successMessage;
    private final Locator amountResult;
    private final Locator fromAccountIdResult;
    private final Locator toAccountIdResult;
    private final Locator toAccountOptions;

    public TransferFundsPage(Page page) {
        this.page = page;
        this.amountInput = page.locator("#amount");
        this.fromAccountSelect = page.locator("#fromAccountId");
        this.toAccountSelect = page.locator("#toAccountId");
        this.transferButton = page.locator("input[type='submit']");
        this.successMessage = page.getByText("Transfer Complete!");
        this.amountResult = page.locator("#amountResult");
        this.fromAccountIdResult = page.locator("#fromAccountIdResult");
        this.toAccountIdResult = page.locator("#toAccountIdResult");
        this.toAccountOptions = page.locator("select#toAccountId option");
    }

    public Locator getSuccessMessage() {
        return successMessage;
    }

    public String getOriginalAccountNumber() {
        return toAccountOptions.first().getAttribute("value");
    }

    public Locator getAmountResult() {
        return amountResult;
    }

    public void transferFunds(int accountToSendFromId, int accountToSendToId, BigDecimal amount) {
        enterAmountIntoAmountInput(amount);
        selectAccountToSendFrom(accountToSendFromId);
        selectAccountToSendTo(accountToSendToId);
        clickTransferButton();
    }

    public void enterAmountIntoAmountInput(BigDecimal amount) {
        String amountString = amount.toPlainString();
        amountInput.fill(amountString);
        assertThat(amountInput).hasValue(amountString);
    }

    public void selectAccountToSendFrom(int accountId) {
        fromAccountSelect.selectOption(String.valueOf(accountId));
    }

    public void selectAccountToSendTo(int accountId) {
        toAccountSelect.selectOption(String.valueOf(accountId));
    }

    public void clickTransferButton() {
        transferButton.click();
    }

    public void assertAmountResultIsTransferAmount(BigDecimal amount) {
        String transferString = amount.toPlainString();
        assertThat(getAmountResult()).hasText("$" + transferString);
    }
}

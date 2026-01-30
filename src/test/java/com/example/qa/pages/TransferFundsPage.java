package com.example.qa.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
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

    public void transferFunds(String accountToSendFrom, String accountToSendTo, int amount) {
        enterAmountIntoAmountInput(String.valueOf(amount));
        selectAccountToSendFrom(accountToSendFrom);
        selectAccountToSendTo(accountToSendTo);
        clickTransferButton();
    }

    public void enterAmountIntoAmountInput(String amount) {
        amountInput.fill(amount);
        assertThat(amountInput).hasValue(amount);
    }

    public void selectAccountToSendFrom(String account) {
        fromAccountSelect.selectOption(account);
    }

    public void selectAccountToSendTo(String account) {
        toAccountSelect.selectOption(account);
    }

    public void clickTransferButton() {
        transferButton.click();
    }
}

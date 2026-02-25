package com.example.qa.pages;

import com.example.qa.enums.AccountType;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.TimeoutError;
import com.microsoft.playwright.options.AriaRole;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class OpenNewAccountPage {

    private final Page page;
    private final Locator accountTypeDropdown;
    private final Locator existingAccountDropdown;
    private final Locator openNewAccountButton;
    private final Locator newAccountIdLink;
    private final Locator openAccountResult;
    private final Locator successfullyOpenedAccountMessage;

    public OpenNewAccountPage(Page page) {
        this.page = page;
        this.accountTypeDropdown = page.locator("select#type.input");
        this.existingAccountDropdown = page.locator("select#fromAccountId.input");
        this.openNewAccountButton = page.locator("input[type='button']");
        this.newAccountIdLink = page.locator("#newAccountId");
        this.openAccountResult = page.locator("#openAccountResult");
        this.successfullyOpenedAccountMessage = openAccountResult.locator(":scope > h1.title");
    }

    //Expose Locators

    public Locator selectedAccount() {
        return accountTypeDropdown.locator("option:checked");
    }

    public Locator successfullyOpenedAccountMessage() {
        return successfullyOpenedAccountMessage;
    }

    public Locator existingAccountDropdown() {
        return existingAccountDropdown;
    }

    public Locator openNewAccountButton() {
        return openNewAccountButton;
    }

    public Locator newAccountIdLink() {
        return newAccountIdLink;
    }

    //Page actions

    //Method includes retry because button is flaky
    public void clickOpenNewAccountButton() {
        openNewAccountButton.click();
        if (!waitForOpenAccountResult()) {
            Locator freshButton = page.getByRole(
                    AriaRole.BUTTON,
                    new Page.GetByRoleOptions().setName("Open New Account")
            );
            assertThat(freshButton).isEnabled();
            freshButton.scrollIntoViewIfNeeded();
            freshButton.click();
            page.waitForSelector("#openAccountResult h1.title");
        }
    }

    public void openNewAccount(AccountType accountType, int seedingAccount) {
        selectAccountTypeFromDropdown(accountType);
        assertThat(selectedAccount()).hasText(accountType.getLabel());
        selectExistingAccountNumberFromDropdown(seedingAccount);
        clickOpenNewAccountButton();
    }

    public void selectAccountTypeFromDropdown(AccountType accountType) {
        accountTypeDropdown.selectOption(accountType.getValue());
    }

    public void selectExistingAccountNumberFromDropdown(int accountNumber) {
        existingAccountDropdown.selectOption(String.valueOf(accountNumber));
    }

    private boolean waitForOpenAccountResult() {
        try {
            page.waitForSelector("#openAccountResult h1.title",
                    new Page.WaitForSelectorOptions().setTimeout(1000));
            return true;
        } catch (TimeoutError e) {
            return false;
        }

    }

    public int getNewAccountNumber() {
        return Integer.parseInt(newAccountIdLink.innerText());
    }
}

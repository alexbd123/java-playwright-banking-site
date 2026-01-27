package com.example.qa.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class OpenNewAccountPage {

    private final Locator accountTypeDropdown;
    private final Locator existingAccountDropdown;
    private final Locator openNewAccountButton;
    private final Locator checkingOptionInDropdown;
    private final Locator savingsOptionInDropdown;
    private final Locator newAccountIdLink;

    public OpenNewAccountPage(Page page) {
        this.accountTypeDropdown = page.locator("select#type.input");
        this.existingAccountDropdown = page.locator("select#fromAccountId.input");
        this.openNewAccountButton = page.locator("input[type='button']");
        this.checkingOptionInDropdown = page.locator("option[value='0']");
        this.savingsOptionInDropdown = page.locator("option[value='1']");
        this.newAccountIdLink = page.locator("#newAccountId");
    }

    //Expose Locators

    public Locator setAccountTypeDropdown() {
        return accountTypeDropdown;
    }

    public Locator setExistingAccountDropdown() {
        return existingAccountDropdown;
    }

    public Locator openNewAccountButton() {
        return openNewAccountButton;
    }

    public Locator checkingOptionInDropdown() {
        return checkingOptionInDropdown;
    }

    public Locator savingsOptionInDropdown() {
        return savingsOptionInDropdown;
    }

    public Locator newAccountIdLink() {
        return newAccountIdLink;
    }

    //Page actions

    public void openAccountTypeDropdown() {
        accountTypeDropdown.click();
    }

    public void openExistingAccountDropdown() {
        existingAccountDropdown.click();
    }

    public void clickOpenNewAccountButton() {
        openNewAccountButton.click();
    }

    public void selectExistingAccountNumberFromDropdown(Page page, String accountNumber) {
        openExistingAccountDropdown();
        Locator existingAccountInDropdown = page.locator("option[value='" + accountNumber + "']");
        existingAccountInDropdown.click();
    }

}

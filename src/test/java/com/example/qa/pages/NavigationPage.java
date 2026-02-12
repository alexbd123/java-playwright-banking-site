package com.example.qa.pages;

import com.example.qa.config.TestConfig;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class NavigationPage {

    private final Page page;
    private final Locator openNewAccountLink;
    private final Locator accountsOverviewLink;
    private final Locator transferFundsLink;
    private final Locator billPayLink;
    private final Locator findTransactionsLink;
    private final Locator updateContactInfoLink;
    private final Locator requestLoanLink;
    private final Locator registrationPageLink;

    public NavigationPage(Page page) {
        this.page = page;
        this.openNewAccountLink = page.locator("//a[contains(text(),'Open New Account')]");
        this.accountsOverviewLink = page.locator("//a[contains(text(),'Accounts Overview')]");
        this.transferFundsLink = page.locator("//a[contains(text(),'Transfer Funds')]").first();
        this.billPayLink = page.locator("//a[contains(text(),'Bill Pay')]");
        this.findTransactionsLink = page.locator("//a[contains(text(),'Find Transactions')]");
        this.updateContactInfoLink = page.locator("//a[contains(text(),'Update Contact Info')]");
        this.requestLoanLink = page.locator("//a[contains(text(),'Request Loan')]");
        this.registrationPageLink = page.locator("//a[contains(text(),'Register')]");
    }

    public void homePage() {
        String url = TestConfig.getBaseUrl();
        page.navigate(url);
    }

    public void registrationPage() {
        registrationPageLink.click();
    }

    public void openNewAccount() {
        openNewAccountLink.click();
    }

    public void accountsOverview() {
        accountsOverviewLink.click();
    }

    public void transferFunds() {
        transferFundsLink.click();
    }

    public void billPay() {
        billPayLink.click();
    }

    public void findTransactions() {
        findTransactionsLink.click();
    }

    public void updateContactInfo() {
        updateContactInfoLink.click();
    }

    public void requestLoan() {
        requestLoanLink.click();
    }

}

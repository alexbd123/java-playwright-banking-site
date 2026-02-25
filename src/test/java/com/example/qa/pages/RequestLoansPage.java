package com.example.qa.pages;

import com.example.qa.enums.UILoanApprovalMessage;
import com.example.qa.pages.ui_dtos.FeLoanResponseDto;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import java.math.BigDecimal;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class RequestLoansPage {

    private final Locator amountInput;
    private final Locator downPaymentInput;
    private final Locator fromAccountSelect;
    private final Locator applyNowButton;
    private final Locator loanProviderName;
    private final Locator loanDate;
    private final Locator loanStatus;
    private final Locator loanErrorAvailableFunds;
    private final Locator loanSuccessMessage;

    public RequestLoansPage(Page page) {

        this.amountInput = page.locator("#amount");
        this.downPaymentInput = page.locator("#downPayment");
        this.fromAccountSelect = page.locator("#fromAccountId");
        this.applyNowButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Apply Now"));
        this.loanProviderName = page.locator("#loanProviderName");
        this.loanDate = page.locator("#responseDate");
        this.loanStatus = page.locator("#loanStatus");
        this.loanErrorAvailableFunds = page.getByText(UILoanApprovalMessage.INSUFFICIENT_AVAILABLE_FUNDS.message);
        this.loanSuccessMessage = page.getByText(UILoanApprovalMessage.LOAN_APPROVED.message);

    }

    public void fillLoanAmount(BigDecimal amount) {
        amountInput.fill(String.valueOf(amount));
    }

    public void fillDownPayment(BigDecimal amount) {
        downPaymentInput.fill(String.valueOf(amount));
    }

    public void selectFromAccountId(int accountId) {
        fromAccountSelect.selectOption(String.valueOf(accountId));
    }

    public void clickApplyNowButton() {
        applyNowButton.click();
    }

    public String getLoanProviderName() {
        return loanProviderName.innerText();
    }

    public String getLoanDate() {
        return loanDate.innerText();
    }

    public String getLoanStatus() {
        return loanStatus.innerText();
    }

    public String getLoanErrorAvailableFunds() {
        return loanErrorAvailableFunds.innerText();
    }

    public String getLoanSuccessMessage() {
        return loanSuccessMessage.innerText();
    }

    public void applyForLoan(BigDecimal loanAmount, BigDecimal downPayment, int accountId) {
        fillLoanAmount(loanAmount);
        fillDownPayment(downPayment);
        selectFromAccountId(accountId);
        clickApplyNowButton();
        assertThat(loanProviderName).isVisible();
    }

    public FeLoanResponseDto toDto(boolean approved) {
        return new FeLoanResponseDto(
                getLoanProviderName(),
                getLoanDate(),
                getLoanStatus(),
                approved ? getLoanSuccessMessage() : getLoanErrorAvailableFunds()
        );
    }

}

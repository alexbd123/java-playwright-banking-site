package com.example.qa.api.dtos;

import com.example.qa.enums.AccountTypes;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountDto {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("customerId")
    private Integer customerId;

    @JsonProperty("type")
    private AccountTypes type;

    @JsonProperty("balance")
    private BigDecimal balance;

    public AccountDto() {}

    public AccountDto(Integer id, Integer customerId, AccountTypes type, BigDecimal balance) {
        this.id = id;
        this.customerId = customerId;
        this.type = type;
        this.balance = balance;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getCustomerId() { return customerId; }
    public void setCustomerId(Integer customerId) { this.customerId = customerId; }

    public AccountTypes getType() { return type; }
    public void setType(AccountTypes type) { this.type = type; }

    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }

}

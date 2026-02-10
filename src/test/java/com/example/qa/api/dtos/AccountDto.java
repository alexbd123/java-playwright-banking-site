package com.example.qa.api.dtos;

import com.example.qa.enums.AccountTypes;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record AccountDto (

    @JsonProperty("id")
    int id,

    @JsonProperty("customerId")
    int customerId,

    @JsonProperty("type")
    AccountTypes type,

    @JsonProperty("balance")
    BigDecimal balance

) {}

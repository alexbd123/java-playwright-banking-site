package com.example.qa.api.dtos;

import com.example.qa.enums.TransactionType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record TransactionDto(

        @JsonProperty("id")
        int id,

        @JsonProperty("accountId")
        int accountId,

        @JsonProperty("type")
        TransactionType type,

        @JsonProperty("date")
        String date,

        @JsonProperty("amount")
        BigDecimal amount,

        @JsonProperty("description")
        String description

) {}
package com.example.qa.api.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CustomerDto(

        @JsonProperty("id")
        int id,

        @JsonProperty("firstName")
        String firstName,

        @JsonProperty("lastName")
        String lastName,

        @JsonProperty("address")
        AddressDto address,

        @JsonProperty("phoneNumber")
        String phoneNumber,

        @JsonProperty("ssn")
        String ssn

) {}
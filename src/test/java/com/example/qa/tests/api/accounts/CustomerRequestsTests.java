package com.example.qa.tests.api.accounts;

import com.example.qa.api.clients.CustomerAPI;
import com.example.qa.api.dtos.AddressDto;
import com.example.qa.api.dtos.CustomerDto;
import com.example.qa.api.dtos.User;
import com.example.qa.api.helpers.ApiHelper;
import com.example.qa.models.UserFactory;
import com.example.qa.tests.base_tests.AuthenticatedBaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CustomerRequestsTests extends AuthenticatedBaseTest {
    protected CustomerAPI customerApi;
    protected ApiHelper helper;
    protected int originalCustomerId;

    @BeforeAll
    void initApiAndGetContext() {
        customerApi = new CustomerAPI(request);
        helper = new ApiHelper(request);
        originalCustomerId = customerContext.getCustomerId();
    }

    @Test
    void updateCustomerDetails() {
        User newUser = UserFactory.validRandomUser();
        CustomerDto expectedCustomerDto = convertUserToCustomerDto(originalCustomerId, newUser);

        customerApi.sendPostRequestToUpdateCustomer(originalCustomerId, newUser);
        CustomerDto actualCustomerDto = customerApi.sendGetRequestForCustomerDetails(originalCustomerId);

        Assertions.assertEquals(expectedCustomerDto.firstName(), actualCustomerDto.firstName());
        Assertions.assertEquals(expectedCustomerDto.lastName(), actualCustomerDto.lastName());
        Assertions.assertEquals(expectedCustomerDto.address(), actualCustomerDto.address());
        Assertions.assertEquals(expectedCustomerDto.phoneNumber(), actualCustomerDto.phoneNumber());
        Assertions.assertEquals(expectedCustomerDto.ssn(), actualCustomerDto.ssn());
    }

    //Helpers
    public CustomerDto convertUserToCustomerDto(int customerId, User user) {
        return new CustomerDto(
                customerId,
                user.firstName(),
                user.lastName(),
                new AddressDto(
                        user.address(),
                        user.city(),
                        user.state(),
                        user.zipCode()
                ),
                user.phoneNumber(),
                user.ssn()
        );
    }
}

package com.example.qa.tests.api.customers;

import com.example.qa.api.clients.CustomerAPI;
import com.example.qa.api.dtos.AddressDto;
import com.example.qa.api.dtos.CustomerDto;
import com.example.qa.api.dtos.User;
import com.example.qa.api.context.builders.UserFactory;
import com.example.qa.tests.base_tests.AuthenticatedBaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CustomerRequestsTests extends AuthenticatedBaseTest {
    protected CustomerAPI customerApi;
    protected int originalCustomerId;

    @BeforeAll
    void initApiAndGetContext() {
        customerApi = new CustomerAPI(request);
        originalCustomerId = customerContext.getCustomerId();
    }

    @Test
    void updateCustomerDetails() {
        User newUser = UserFactory.validRandomUser();
        CustomerDto expectedCustomer = convertUserToCustomerDto(originalCustomerId, newUser);
        String expectedSuccessResponse = "Successfully updated customer profile";

        String actualSuccessResponse = customerApi.sendPostRequestToUpdateCustomer(originalCustomerId, newUser);
        Assertions.assertEquals(expectedSuccessResponse, actualSuccessResponse);
        CustomerDto actualCustomer = customerApi.sendGetRequestForCustomerDetails(originalCustomerId);

        Assertions.assertEquals(expectedCustomer.firstName(), actualCustomer.firstName());
        Assertions.assertEquals(expectedCustomer.lastName(), actualCustomer.lastName());
        Assertions.assertEquals(expectedCustomer.address(), actualCustomer.address());
        Assertions.assertEquals(expectedCustomer.phoneNumber(), actualCustomer.phoneNumber());
        Assertions.assertEquals(expectedCustomer.ssn(), actualCustomer.ssn());
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

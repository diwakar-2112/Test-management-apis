package com.testPortal.test_management_api.testsuite.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateTestSuiteRequest {
    @NotBlank(message = "Test suite name cannot be blank.")
    @Size(min = 3, max = 100, message = "Test suite name must be between 3 and 100 characters.")
    private String name;
}

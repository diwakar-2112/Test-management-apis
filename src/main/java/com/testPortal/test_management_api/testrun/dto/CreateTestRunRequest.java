package com.testPortal.test_management_api.testrun.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateTestRunRequest {

    @NotBlank(message = "Test run name cannot be blank.")
    @Size(min = 3, max = 150, message = "Test run name must be between 3 and 150 characters.")
    private String name;
}
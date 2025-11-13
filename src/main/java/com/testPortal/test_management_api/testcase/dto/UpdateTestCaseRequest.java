package com.testPortal.test_management_api.testcase.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateTestCaseRequest {

    @NotBlank(message = "Test case title cannot be blank.")
    @Size(max = 255, message = "Title cannot be longer than 255 characters.")
    private String title;

    private String description;

    private String steps;

    private String expectedResult;
}
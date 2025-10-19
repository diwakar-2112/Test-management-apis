package com.testPortal.test_management_api.testcase.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data

public class CreateTestCaseRequest {

    @NotBlank(message = "Test case can't be blank.")
    @Size(max = 255,message = "Title can't be longer than 255 character")
    private String title;

    private String description;

    private String steps;

    private String expectedResult;
}

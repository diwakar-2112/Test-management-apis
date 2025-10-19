package com.testPortal.test_management_api.testcase.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
public class TestCaseResponse {

    private Integer id;
    private String title;
    private String description;
    private String steps;
    private String expectedResult;
    private Integer testSuitId;  // Include the parent's ID for context

}

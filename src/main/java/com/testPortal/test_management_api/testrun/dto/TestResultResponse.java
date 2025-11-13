package com.testPortal.test_management_api.testrun.dto;

import com.testPortal.test_management_api.testrun.TestResultStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TestResultResponse {
    private Integer id;
    private TestResultStatus status;
    private String comments;
    private String title;
    private String description;
    private String steps;
    private String expectedResult;
}
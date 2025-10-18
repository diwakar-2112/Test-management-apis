package com.testPortal.test_management_api.testsuite.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TestSuiteResponse {
    private Integer id;
    private String name;
    private Integer projectId; // We include the parent project's ID for context.
}

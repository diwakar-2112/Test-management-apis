package com.testPortal.test_management_api.testrun.dto;

import com.testPortal.test_management_api.testrun.TestRunStatus;
import lombok.Data;

@Data
public class TestRunSearchCriteria {
    private String name;
    private Integer projectId;
    private TestRunStatus status;
    private Integer assigneeId;
}

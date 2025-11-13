package com.testPortal.test_management_api.testrun.dto;

import com.testPortal.test_management_api.testrun.TestResultStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateTestResultRequest {

    @NotNull(message = "Status cannot be null.")
    private TestResultStatus status;

    private String comments;
}
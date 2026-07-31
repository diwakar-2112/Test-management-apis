package com.testPortal.test_management_api.testrun;

import com.testPortal.test_management_api.common.PagedResponse;
import com.testPortal.test_management_api.testrun.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/api/testsuites/{suiteId}/testruns", "/api/testruns"})
public class TestRunController {

    private final TestRunService testRunService;

    public TestRunController(TestRunService testRunService) {
        this.testRunService = testRunService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public TestRunResponse startTestRun(@PathVariable Integer suiteId, @Valid @RequestBody CreateTestRunRequest request) {
        return testRunService.startTestRunFromSuite(suiteId, request);
    }

    @PostMapping("/{runId}/assign")
    @PreAuthorize("hasRole('ADMIN')")
    public TestRunResponse assignTestRun(@PathVariable Integer runId, @RequestParam Integer userId) {
        return testRunService.assignTestRun(runId, userId);
    }

    // SIMPLIFIED: No more .orElseThrow()
    @GetMapping("/{runId}")
    public TestRunResponse getTestRunDetails(@PathVariable Integer runId) {
        return testRunService.getTestRunDetails(runId);
    }

    @PutMapping("/results/{resultId}")
//    @PreAuthorize("hasRole('ADMIN')")
    public TestResultResponse updateResult(@PathVariable Integer resultId, @Valid @RequestBody UpdateTestResultRequest request) {
        return testRunService.updateTestResult(resultId, request);
    }

    @GetMapping
    public PagedResponse<TestRunResponse> getAllTestRuns(
            TestRunSearchCriteria searchCriteria,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String soryBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ){
        return testRunService.getAllTestRuns(searchCriteria,page,size,soryBy,sortDir);
    }
}


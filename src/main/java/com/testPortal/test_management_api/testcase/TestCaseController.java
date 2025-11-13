package com.testPortal.test_management_api.testcase;

import com.testPortal.test_management_api.testcase.dto.CreateTestCaseRequest;
import com.testPortal.test_management_api.testcase.dto.TestCaseResponse;
import com.testPortal.test_management_api.testcase.dto.UpdateTestCaseRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.web.bind.annotation.*;

import java.util.List;



@RestController
@RequestMapping({"/api/testsuites/{suiteId}/testcase","/api/testcases"})
public class TestCaseController {
    private final TestCaseService testCaseService;

    public TestCaseController(TestCaseService testCaseService){
        this.testCaseService=testCaseService;
    }

    //POST /api/testsuites/{suiteId}/testcase
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TestCaseResponse createTestCase(@PathVariable Integer suiteId,@Valid @RequestBody CreateTestCaseRequest request){
        return testCaseService.createTestCaseForSuite(suiteId,request);
    }

    //GET
    @GetMapping
    public List<TestCaseResponse> getTestCaseForSuite(@PathVariable Integer suiteId){
        return testCaseService.getTestCaseForSuit(suiteId);
    }

    @PutMapping("/{caseId}")
    public TestCaseResponse updateTestCase(@PathVariable Integer caseId, @Valid @RequestBody UpdateTestCaseRequest request) {
        return testCaseService.updateTestCase(caseId, request)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "TestCase not found with id: " + caseId));
    }

    @DeleteMapping("/{caseId}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // Standard response for a successful delete
    public void deleteTestCase(@PathVariable Integer caseId) {
        boolean deleted = testCaseService.deleteTestCase(caseId);
        if (!deleted) {
            // If the service returns false, it means the test case was not found.
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "TestCase not found with id: " + caseId);
        }
    }



}

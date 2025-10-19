package com.testPortal.test_management_api.testcase;

import com.testPortal.test_management_api.testcase.dto.CreateTestCaseRequest;
import com.testPortal.test_management_api.testcase.dto.TestCaseResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.*;

import java.util.List;



@RestController
@RequestMapping("/api/testsuites/{suiteId}/testcase")
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

}

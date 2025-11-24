package com.testPortal.test_management_api.testsuite;


import com.testPortal.test_management_api.project.dto.CreateProjectRequest;
import com.testPortal.test_management_api.testsuite.dto.CreateTestSuiteRequest;
import com.testPortal.test_management_api.testsuite.dto.TestSuiteResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;

import  org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;


@RestController
// IMPORTANT: The base URL for this controller is nested.
// It means all endpoints defined here will be prefixed with "/api/projects/{projectId}".
@RequestMapping({"/api/projects/{projectId}/testsuites","/api/testsuites"})
public class TestSuiteController {
    private final TestSuiteService testSuiteService;

    public TestSuiteController(TestSuiteService testSuiteService){
        this.testSuiteService=testSuiteService;
    }

    //// POST /api/projects/{projectId}/testsuites
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
   public TestSuiteResponse createTestSuite(@PathVariable Integer projectId,@Valid @RequestBody CreateTestSuiteRequest request){
        return testSuiteService.createTestSuiteForProject(projectId,request);
    }

    // GET /api/projects/{projectId}/testsuites
    @GetMapping
    public List<TestSuiteResponse> getTestSuitesForProject(@PathVariable Integer projectId){
        System.out.println(testSuiteService.getTestSuitesForProject(projectId));
        return testSuiteService.getTestSuitesForProject(projectId);
    }
    // PUT /api/testsuites/{suiteId}
    @PutMapping("/{suiteId}")
    public TestSuiteResponse updateTestSuite(@PathVariable Integer suiteId, @Valid @RequestBody CreateTestSuiteRequest request){
        return testSuiteService.updateTestSuite(suiteId, request);
    }

    //Delete
    @DeleteMapping("/{suiteId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTestSuite(@PathVariable Integer suiteId){
        testSuiteService.deleteTestSuite(suiteId);
    }


}

package com.testPortal.test_management_api.testsuite;


import com.testPortal.test_management_api.project.dto.CreateProjectRequest;
import com.testPortal.test_management_api.testsuite.dto.CreateTestSuiteRequest;
import com.testPortal.test_management_api.testsuite.dto.TestSuiteResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import  org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
 import com.testPortal.test_management_api.common.PagedResponse;

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
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasAuthority('TESTSUITE_CREATE')")
   public TestSuiteResponse createTestSuite(@PathVariable Integer projectId,@Valid @RequestBody CreateTestSuiteRequest request){
        return testSuiteService.createTestSuiteForProject(projectId,request);
    }

    // GET /api/projects/{projectId}/testsuites
    @GetMapping
    public ResponseEntity<?> getTestSuitesForProject(
            @PathVariable Integer projectId,
            @RequestParam(defaultValue = "false") boolean isAll,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        if(isAll){
            List<TestSuiteResponse> allSuites = testSuiteService.getAllTestSuitesForProject(projectId);
            return ResponseEntity.ok(allSuites);
        }

        else{
            PagedResponse<TestSuiteResponse> pagedSuites = testSuiteService.getTestSuitesForProject(projectId, page, size, sortBy, sortDir);
            // This is also valid because the method's return type is ResponseEntity.
            return ResponseEntity.ok(pagedSuites);
        }
    }
    // PUT /api/testsuites/{suiteId}
    @PutMapping("/{suiteId}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasAuthority('TESTSUITE_EDIT')")
    public TestSuiteResponse updateTestSuite(@PathVariable Integer suiteId, @Valid @RequestBody CreateTestSuiteRequest request){
        return testSuiteService.updateTestSuite(suiteId, request);
    }

    //Delete
    @DeleteMapping("/{suiteId}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasAuthority('TESTSUITE_DELETE')")
//    @ResponseStatus(HttpStatus.NO_CONTENT)
    public TestSuiteResponse  deleteTestSuite(@PathVariable Integer suiteId){
      return  testSuiteService.deleteTestSuite(suiteId);
    }


}

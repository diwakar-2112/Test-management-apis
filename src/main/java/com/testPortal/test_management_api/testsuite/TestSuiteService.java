package com.testPortal.test_management_api.testsuite;

import com.testPortal.test_management_api.project.Project;
import com.testPortal.test_management_api.project.ProjectRepository;
import com.testPortal.test_management_api.project.dto.CreateProjectRequest;
import com.testPortal.test_management_api.testsuite.dto.CreateTestSuiteRequest;
import com.testPortal.test_management_api.testsuite.dto.TestSuiteResponse;

import org.aspectj.weaver.ast.Test;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TestSuiteService {

    private final TestSuiteRepository testSuiteRepository;
    private final ProjectRepository projectRepository;

    public TestSuiteService(TestSuiteRepository testSuiteRepository, ProjectRepository projectRepository) {
        this.testSuiteRepository = testSuiteRepository;
        this.projectRepository = projectRepository;
    }

    /**
     * Creates a new Test Suite and associates it with a parent Project.
     */

    public TestSuiteResponse createTestSuiteForProject(Integer projectId, CreateTestSuiteRequest request) {
        // 1. Find the parent Project. If it doesn't exist, throw a 404 error.
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found with id:" + projectId));

        // 2. Create a new TestSuite entity.
        TestSuite newTestSuite = new TestSuite();
        newTestSuite.setName(request.getName());

        // 3. THIS IS THE CRITICAL STEP: Set the parent project on the child.
        // This is what creates the link (the foreign key relationship) in the database.
        newTestSuite.setProject(project);

        // 4. Save the new TestSuite. JPA handles the rest.
        TestSuite savedTestSuite = testSuiteRepository.save(newTestSuite);


        // 5. Convert to a response DTO and return.
        return convertToResponse(savedTestSuite);
    }

    /**
     * Finds all Test Suites for a given Project ID.
     */

    public List<TestSuiteResponse> getTestSuitesForProject(Integer projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found with id:" + projectId);
        }
        return testSuiteRepository.findByProjectId(projectId).stream().map(this::convertToResponse).collect(Collectors.toList());
    }


    //    update -> rename the existing testsuite
    public Optional<TestSuiteResponse> updateTestSuite(Integer suiteId, CreateTestSuiteRequest request) {

        // 1. Find the existing test suite by its own ID.
        return testSuiteRepository.findById(suiteId)
                .map(existingTestSuite -> {  //{id} if exits
//                        update its properties
                    existingTestSuite.setName(request.getName());

                    //save it back to db
                    TestSuite updatedSuite = testSuiteRepository.save(existingTestSuite);
                    // convert it to dto and return
                    return convertToResponse(updatedSuite);
                });
    }

//    Delete
    public  void deleteTestSuite(Integer suiteId){
        if(!testSuiteRepository.existsById(suiteId)){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"No test suite found with the id:"+suiteId);
        }
        testSuiteRepository.deleteById(suiteId);
    }

    // --- Helper Method for DTO Conversion ---

    private TestSuiteResponse convertToResponse(TestSuite testSuite) {
        return new TestSuiteResponse(
                testSuite.getId(),
                testSuite.getName(),
                testSuite.getProject().getId() // Get the ID from the nested project object
        );
    }

}

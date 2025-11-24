package com.testPortal.test_management_api.testsuite;

import com.testPortal.test_management_api.exception.ResourceNotFoundException;
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
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));

        TestSuite newTestSuite = new TestSuite();
        newTestSuite.setName(request.getName());
        newTestSuite.setProject(project);
        TestSuite savedTestSuite = testSuiteRepository.save(newTestSuite);
        return convertToResponse(savedTestSuite);
    }

    /**
     * Finds all Test Suites for a given Project ID.
     */

    public List<TestSuiteResponse> getTestSuitesForProject(Integer projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new ResourceNotFoundException("Project not found with id: " + projectId);
        }
        return testSuiteRepository.findByProjectId(projectId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }


    //    update -> rename the existing testsuite
    public TestSuiteResponse updateTestSuite(Integer suiteId, CreateTestSuiteRequest request) {
        TestSuite existingTestSuite = testSuiteRepository.findById(suiteId)
                .orElseThrow(() -> new ResourceNotFoundException("TestSuite not found with id: " + suiteId));

        existingTestSuite.setName(request.getName());
        TestSuite updatedSuite = testSuiteRepository.save(existingTestSuite);
        return convertToResponse(updatedSuite);
    }

    //    Delete
    public void deleteTestSuite(Integer suiteId){
        if(!testSuiteRepository.existsById(suiteId)){
            throw new ResourceNotFoundException("TestSuite not found with id: " + suiteId);
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

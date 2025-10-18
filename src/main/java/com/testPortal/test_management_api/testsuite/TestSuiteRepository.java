package com.testPortal.test_management_api.testsuite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestSuiteRepository extends JpaRepository<TestSuite,Integer> {

    // Spring Data JPA is incredibly smart. By defining a method with this exact name,
    // it will automatically generate a query that finds all TestSuite entities
    // where the 'project' field's 'id' property matches the given projectId.
    // SQL Equivalent: "SELECT * FROM test_suites WHERE project_id = ?"
    List<TestSuite> findByProjectId(Integer projectId);

}

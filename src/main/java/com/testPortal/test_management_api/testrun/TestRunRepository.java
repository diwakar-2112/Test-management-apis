package com.testPortal.test_management_api.testrun;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestRunRepository extends JpaRepository<TestRun, Integer> {
    // Spring Data JPA will provide all the standard CRUD methods like findById(), save(), etc.
    // We can add custom query methods here later if needed.
}
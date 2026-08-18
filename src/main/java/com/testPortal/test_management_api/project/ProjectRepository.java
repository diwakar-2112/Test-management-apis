package com.testPortal.test_management_api.project;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;



// @Repository tells Spring that this is a Repository bean. It's not strictly necessary
// because JpaRepository is already a known type, but it's good practice for clarity.

@Repository
// We extend JpaRepository. We must provide two pieces of information in the angle brackets:
// 1. The type of the Entity this repository manages (Project).
// 2. The data type of the primary key of that Entity (Integer).

public interface ProjectRepository extends JpaRepository<Project, Integer> {
    // That's it!
    // By extending JpaRepository, we automatically inherit methods like:
    // - save(Project project) -> Creates or updates a project
    // - findAll() -> Returns a List<Project>
    // - findById(Integer id) -> Returns an Optional<Project>
    // - deleteById(Integer id) -> Deletes a project
    // ...and many more! Spring Data JPA writes the implementation for us.

    //1. For the Paginated table
    // This JPQL query uses a JOIN to link the Project to its TestRuns, and checks the assignee ID.
    // 'DISTINCT' ensures that if a user has 5 runs in Project A, Project A only shows up once in the list.
//    @Query("SELECT DISTINCT p FROM Project p JOIN p.testRuns tr WHERE tr.assignee.id = :userId")
//    Page<Project> findProjectByAssigneeId(@Param("userId") Integer userId,Pageable pageable);

    @Query("SELECT DISTINCT p FROM Project p LEFT JOIN p.testRuns tr WHERE tr.assignee.id = :userId OR p.createdBy.id = :userId")
    Page<Project> findProjectByAssigneeId(@Param("userId") Integer userId, Pageable pageable);

    // 2. For the isAll=true Dropdown Menu
//    @Query("SELECT DISTINCT p FROM Project p JOIN p.testRuns tr WHERE tr.assignee.id = :userId")
//    List<Project> findProjectsByAssigneeIdList(@Param("userId") Integer userId);

    @Query("SELECT DISTINCT p FROM Project p LEFT JOIN p.testRuns tr WHERE tr.assignee.id = :userId OR p.createdBy.id = :userId")
    List<Project> findProjectsByAssigneeIdList(@Param("userId") Integer userId);
}

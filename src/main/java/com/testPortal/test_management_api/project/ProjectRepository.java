package com.testPortal.test_management_api.project;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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
}

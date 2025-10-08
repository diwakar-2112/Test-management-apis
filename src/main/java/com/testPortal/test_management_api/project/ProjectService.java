package com.testPortal.test_management_api.project;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

// @Service tells Spring that this class is a "Service". It's a stereotype annotation
// that makes this class a candidate for dependency injection.
@Service

public class ProjectService {
    // We move the list of projects here.
    // We use ArrayList because we need to be able to ADD new projects to this list.
    private final List<Project> projects = new ArrayList<>();

    // This will act as our fake ID generator, like a database sequence.
    private final AtomicInteger counter = new AtomicInteger();


    // This block runs when the service is first created.
    // It pre-populates our list with some initial data.
    public ProjectService() {
        projects.add(new Project(counter.incrementAndGet(), "E-commerce Website", "Test suite for the new online store."));
        projects.add(new Project(counter.incrementAndGet(), "Mobile Banking App", "Test suite for the iOS and Android banking app."));
    }

    /**
     * Returns a list of all projects.
     */
    public List<Project> findAll() {
        return projects;
    }

    /**
     * Creates a new project.
     * @param project The project data to create.
     * @return The newly created project with its assigned ID.
     */
    public Project create(Project project) {
        // In a real app, the database would set the ID. Here, we set it manually.
        project.setId(counter.incrementAndGet());
        projects.add(project);
        return project;
    }
}

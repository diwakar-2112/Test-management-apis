package com.testPortal.test_management_api.project;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")

public class ProjectController {

    //we declare the service we need
    private final ProjectService projectService;

    // This is called "Constructor Injection".
    // Spring sees that the controller needs a ProjectService to be created,
    // so it automatically provides the one we created earlier. This is a core concept of Spring.
    public ProjectController(ProjectService projectService){
        this.projectService=projectService;
    }

    @GetMapping
    public List<Project>findAll(){
        return projectService.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Project create(@RequestBody Project project){
        return projectService.create(project);
    }

}

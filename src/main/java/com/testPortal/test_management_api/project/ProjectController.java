package com.testPortal.test_management_api.project;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// @RestController tells Spring this class is for handling API requests.
// It combines @Controller and @ResponseBody, which means methods will
// return data (like JSON) directly, not a webpage.
@RestController

// @RequestMapping("/api/projects") sets a base URL for all endpoints in this class.
// So, all URLs for this controller will start with "/api/projects".
@RequestMapping("/api/projects")
public class ProjectController {

    // @GetMapping marks this method to handle HTTP GET requests.
    // Since the class has a base URL of "/api/projects", this method
    // will be triggered for GET requests to "/api/projects".
    @GetMapping
    public List<Project> findAll(){
        // This is our fake data for now.
        // Later, we will get this from a real database.
        return List.of(
                new Project(1, "E-commerce Website", "Test suite for the new online store."),
                new Project(2, "Mobile Banking App", "Test suite for the iOS and Android banking app."),
                new Project(3, "Inventory Management System", "Testing for the warehouse inventory system.")
        );
    }
}

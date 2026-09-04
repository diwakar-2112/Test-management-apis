package com.testPortal.test_management_api.project;

import com.testPortal.test_management_api.common.LookupItem;
import com.testPortal.test_management_api.common.LookupProvider;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;


@Component
public class ProjectLookupProvider implements LookupProvider {
    private final ProjectRepository projectRepository;

    public ProjectLookupProvider(ProjectRepository projectRepository){
        this.projectRepository=projectRepository;
    }

    @Override
    public String getType(){
        return "projects";
    }
    @Override
    public List<LookupItem> getDropdownItems(){
        return projectRepository.findAll().stream()
                .map(project-> new LookupItem(project.getId().toString(),project.getName()))
                .collect(Collectors.toList());
    }


}

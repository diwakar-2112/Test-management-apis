package com.testPortal.test_management_api.administration;

import com.testPortal.test_management_api.administration.dto.ModuleDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;



@Service
public class ModuleService {
    private final ModuleRepository moduleRepository;

    public ModuleService(ModuleRepository moduleRepository){
        this.moduleRepository = moduleRepository;
    }

    public ModuleDto createModule(ModuleDto request){
        if(moduleRepository.existsByModuleKey(request.getModuleKey())){
            throw new RuntimeException("Module Key already exists!");
        }

        Module module = new Module();
        module.setModuleName(request.getModuleName());
        module.setModuleUrl(request.getModuleUrl());
        module.setModuleKey(request.getModuleKey());

        Module savedModule = moduleRepository.save(module);

        return new ModuleDto(savedModule.getId(), savedModule.getModuleName(), savedModule.getModuleUrl(), savedModule.getModuleName());

    }

    public List<ModuleDto> getAllModules(){
        return moduleRepository.findAll().stream().map(m->new ModuleDto(m.getId(),m.getModuleName(),m.getModuleUrl(),m.getModuleKey())).collect(Collectors.toList());
    }
}

package com.testPortal.test_management_api.administration;

import com.testPortal.test_management_api.administration.dto.CreateRoleRequest;
import com.testPortal.test_management_api.administration.dto.RoleDto;
import com.testPortal.test_management_api.administration.dto.RoleModuleAccessDto;
import com.testPortal.test_management_api.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class RoleService {
    private final RoleRepository roleRepository;
    private final ModuleRepository moduleRepository;

    public RoleService(RoleRepository roleRepository,ModuleRepository moduleRepository){
        this.roleRepository=roleRepository;
        this.moduleRepository=moduleRepository;
    }

    //create role
    @Transactional
    public RoleDto createRoleDto(CreateRoleRequest request){
        if(roleRepository.existsByRoleName(request.getRoleName())){
            throw new RuntimeException("Role already exists!");
        }
        Role role = new Role();
        role.setRoleName(request.getRoleName());

        // Map the DTO checkboxes(permission of modules) into Entities
        List<RoleModuleAccess> accesses = request.getModuleAccess().stream().map(accessDto ->{
            Module module = moduleRepository.findById(accessDto.getModuleId())
                    .orElseThrow(()-> new ResourceNotFoundException("Module not found with ID: "+accessDto.getModuleId()));

            RoleModuleAccess access = new RoleModuleAccess();
            access.setRole(role);
            access.setModule(module);
            access.setCanCreate(access.isCanCreate());
            access.setCanEdit(accessDto.isCanEdit());
            access.setCanDelete(accessDto.isCanDelete());
            access.setCanList(accessDto.isCanList());
            access.setCanView(accessDto.isCanView());

            return access;
        }).collect(Collectors.toList());
        role.setModuleAccessList(accesses);
        Role savedRole = roleRepository.save(role);
        return convertToDto(savedRole);
    }

    //get all role
    public List<RoleDto> getAllRoles(){
        return roleRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    //get by id
    public RoleDto getRoleById(Integer id){
        Role role = roleRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Role not found with id: "+id));
        return convertToDto(role);
    }

    //delete
    public void deleteRole(Integer id){
        if(!roleRepository.existsById(id)){
            throw new ResourceNotFoundException("Role not found with id: "+id);
        }
        roleRepository.deleteById(id);
    }


    private RoleDto convertToDto(Role role) {
        List<RoleModuleAccessDto> accessDtos = role.getModuleAccessList().stream().map(access ->
                new RoleModuleAccessDto(
                        access.getModule().getId(),
                        access.getModule().getModuleName(),
                        access.isCanCreate(),
                        access.isCanEdit(),
                        access.isCanDelete(),
                        access.isCanList(),
                        access.isCanView()
                )
        ).collect(Collectors.toList());

        return new RoleDto(role.getId(), role.getRoleName(), accessDtos);
    }
}

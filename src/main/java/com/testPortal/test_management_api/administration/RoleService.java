package com.testPortal.test_management_api.administration;

import com.testPortal.test_management_api.administration.dto.CreateRoleRequest;
import com.testPortal.test_management_api.administration.dto.RoleDto;
import com.testPortal.test_management_api.administration.dto.RoleModuleAccessDto;
import com.testPortal.test_management_api.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
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
            access.setCanCreate(accessDto.isCanCreate());
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
        // 1. Fetch EVERY module in the system
        List<Module> allModules = moduleRepository.findAll();
        // 2. Create a quick lookup map of the permissions this Role actually has saved in the DB
        Map<Integer, RoleModuleAccess> savedAccessMap = role.getModuleAccessList().stream()
                .collect(Collectors.toMap(access -> access.getModule().getId(), access -> access));
        // 3. Build the complete matrix for the frontend
        List<RoleModuleAccessDto> completeMatrix = allModules.stream().map(module -> {

            if (savedAccessMap.containsKey(module.getId())) {
                // Scenario A: The role has saved permissions for this module. Use them!
                RoleModuleAccess access = savedAccessMap.get(module.getId());
                return new RoleModuleAccessDto(
                        module.getId(),
                        module.getModuleName(),
                        access.isCanCreate(),
                        access.isCanEdit(),
                        access.isCanDelete(),
                        access.isCanList(),
                        access.isCanView()
                );
            } else {
                // Scenario B: The role has NO permissions for this module yet. Send all 'false'!
                return new RoleModuleAccessDto(
                        module.getId(),
                        module.getModuleName(),
                        false, false, false, false, false
                );
            }

        }).collect(Collectors.toList());
        // 4. Return the Role with the fully populated matrix
        return new RoleDto(role.getId(), role.getRoleName(), completeMatrix);
    }

    //delete
    public void deleteRole(Integer id){
        if(!roleRepository.existsById(id)){
            throw new ResourceNotFoundException("Role not found with id: "+id);
        }
        roleRepository.deleteById(id);
    }
    @Transactional
    public RoleDto updateRole(Integer id, CreateRoleRequest request) {
        // 1. Find the existing role
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with ID: " + id));

        // 2. Check if they are trying to rename it to a name that already exists (but ignore if it's the same name)
        if (!role.getRoleName().equals(request.getRoleName()) && roleRepository.existsByRoleName(request.getRoleName())) {
            throw new RuntimeException("Role name already exists!");
        }

        // 3. Update the Role Name
        role.setRoleName(request.getRoleName());

        // 4. CLEAR the old permissions. Hibernate's orphanRemoval will auto-delete them from the DB!
        role.getModuleAccessList().clear();

        // 5. Build the new permissions from the Request
        List<RoleModuleAccess> newAccesses = request.getModuleAccess().stream().map(accessDto -> {
            Module module = moduleRepository.findById(accessDto.getModuleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Module not found with ID: " + accessDto.getModuleId()));

            RoleModuleAccess access = new RoleModuleAccess();
            access.setRole(role);
            access.setModule(module);
            access.setCanCreate(accessDto.isCanCreate());
            access.setCanEdit(accessDto.isCanEdit());
            access.setCanDelete(accessDto.isCanDelete());
            access.setCanList(accessDto.isCanList());
            access.setCanView(accessDto.isCanView());

            return access;
        }).collect(Collectors.toList());

        // 6. Add the new permissions and save!
        role.getModuleAccessList().addAll(newAccesses);

        Role updatedRole = roleRepository.save(role);
        return convertToDto(updatedRole);
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

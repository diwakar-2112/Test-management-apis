package com.testPortal.test_management_api.administration;

import com.testPortal.test_management_api.common.LookupItem;
import com.testPortal.test_management_api.common.LookupProvider;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;


@Component
public class RoleLookupProvider implements LookupProvider {
    private final RoleRepository roleRepository;
    public RoleLookupProvider(RoleRepository roleRepository){
        this.roleRepository = roleRepository;
    }

    @Override
    public String getType(){
        return "roles";
    }

    @Override
    public List<LookupItem> getDropdownItems(){
        return roleRepository.findAll().stream().map(role-> new LookupItem(role.getId().toString(),role.getRoleName())).collect(Collectors.toList());
    }
}

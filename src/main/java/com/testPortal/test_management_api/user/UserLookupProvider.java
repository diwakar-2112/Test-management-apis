package com.testPortal.test_management_api.user;

import com.testPortal.test_management_api.common.LookupItem;
import com.testPortal.test_management_api.common.LookupProvider;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserLookupProvider implements LookupProvider {
    private final UserRepository userRepository;
    public UserLookupProvider(UserRepository userRepository){
        this.userRepository=userRepository;
    }

    @Override
    public String getType(){
        return "users";
    }

    @Override
    public List<LookupItem> getDropdownItems(){
        return userRepository.findAll().stream()
                .filter(user->user.getStatus()==UserStatus.ACTIVE)
                .map(user->new LookupItem(user.getId().toString(),user.getUsername()))
                .collect(Collectors.toList());
    }
}

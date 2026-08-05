package com.testPortal.test_management_api;

import com.testPortal.test_management_api.administration.*;
import com.testPortal.test_management_api.administration.Module;
import com.testPortal.test_management_api.user.User;
import com.testPortal.test_management_api.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner  {
    private final ModuleRepository moduleRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(ModuleRepository moduleRepository, RoleRepository roleRepository,
                            UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.moduleRepository = moduleRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String ...args) throws Exception{
        if (moduleRepository.count() == 0) {
            System.out.println("Seeding Modules...");
            moduleRepository.save(new Module(null, "Projects", "/projects", "PROJECT"));
            moduleRepository.save(new Module(null, "Test Suites", "/test-suites", "TESTSUITE"));
            moduleRepository.save(new Module(null, "Test Cases", "/test-cases", "TESTCASE"));
            moduleRepository.save(new Module(null, "Test Runs", "/test-runs", "TESTRUN"));
            moduleRepository.save(new Module(null, "User Management", "/users", "USER_MGMT"));
            moduleRepository.save(new Module(null, "Role Management", "/roles", "ROLE_MGMT"));
        }
        List<Module> allModules = moduleRepository.findAll();

        // 2. CREATE SUPER_ADMIN ROLE (If it doesn't exist)
        if (!roleRepository.existsByRoleName("SUPER_ADMIN")) {
            System.out.println("Seeding SUPER_ADMIN Role...");
            Role adminRole = new Role();
            adminRole.setRoleName("SUPER_ADMIN");

            List<RoleModuleAccess> adminAccess = new ArrayList<>();
            for (Module mod : allModules) {
                RoleModuleAccess access = new RoleModuleAccess();
                access.setRole(adminRole);
                access.setModule(mod);
                // Admin gets TRUE for everything
                access.setCanCreate(true); access.setCanEdit(true); access.setCanDelete(true);
                access.setCanList(true); access.setCanView(true);
                adminAccess.add(access);
            }
            adminRole.setModuleAccessList(adminAccess);
            roleRepository.save(adminRole);
        }

        // 3. CREATE DEFAULT TESTER ROLE (So Registration doesn't crash)
        if (!roleRepository.existsByRoleName("TESTER")) {
            System.out.println("Seeding TESTER Role...");
            Role testerRole = new Role();
            testerRole.setRoleName("TESTER");

            List<RoleModuleAccess> testerAccess = new ArrayList<>();
            for (Module mod : allModules) {
                RoleModuleAccess access = new RoleModuleAccess();
                access.setRole(testerRole);
                access.setModule(mod);

                // Testers can't delete anything, and they can't access User/Role Management at all
                if (mod.getModuleKey().equals("USER_MGMT") || mod.getModuleKey().equals("ROLE_MGMT")) {
                    access.setCanCreate(false); access.setCanEdit(false); access.setCanDelete(false);
                    access.setCanList(false); access.setCanView(false);
                } else {
                    access.setCanCreate(true); access.setCanEdit(true); access.setCanDelete(false);
                    access.setCanList(true); access.setCanView(true);
                }
                testerAccess.add(access);
            }
            testerRole.setModuleAccessList(testerAccess);
            roleRepository.save(testerRole);
        }

        // 4. CREATE DEFAULT ADMIN USER (If not exists)
        if (userRepository.findByUsername("superadmin").isEmpty()) {
            System.out.println("Seeding default Admin User...");
            User adminUser = new User();
            adminUser.setUsername("superadmin");
            adminUser.setPassword(passwordEncoder.encode("superadmin123$"));
            System.out.println("username is: "+adminUser.getUsername()+".............................");
            Role superAdminRole = roleRepository.findByRoleName("SUPER_ADMIN").get();
            adminUser.setRole(superAdminRole);

            userRepository.save(adminUser);
        }

        System.out.println("Database Seeding Completed Successfully!");
    }
}

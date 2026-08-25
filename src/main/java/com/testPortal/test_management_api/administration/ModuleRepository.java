package com.testPortal.test_management_api.administration;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



public interface ModuleRepository extends JpaRepository<Module,Integer> {

    boolean existsByModuleKey(String moduleKey);
}

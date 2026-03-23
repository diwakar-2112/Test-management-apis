package com.testPortal.test_management_api.testrun;

import com.testPortal.test_management_api.testrun.dto.TestRunSearchCriteria;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;





public class TestRunSpecifications {

    public static Specification<TestRun> withDynamicQuery(TestRunSearchCriteria criteria){
        return (root,query,criteriaBuilder)->{
            // We create a list to hold all our WHERE conditions (predicates)
            List<Predicate> predicates = new ArrayList<>();
            // 1. Filter by Name (using LIKE for partial matching, and making it case-insensitive)
            if(criteria.getName() != null && !criteria.getName().trim().isEmpty()){
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")),"%" + criteria.getName().toLowerCase()+"%"));
            }
            //2. Filter by projectId
            if(criteria.getProjectId()!=null){
                predicates.add(criteriaBuilder.equal(root.get("project").get("id"),criteria.getProjectId()));
            }
            //3. Filter by status
            if(criteria.getStatus() != null){
                predicates.add(criteriaBuilder.equal(root.get("status"),criteria.getStatus()));
            }

            //4. Filter by Assignee Id
            if(criteria.getAssigneeId() != null){
                predicates.add(criteriaBuilder.equal(root.get("assignee").get("id"),criteria.getAssigneeId()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}

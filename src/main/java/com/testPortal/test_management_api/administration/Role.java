package com.testPortal.test_management_api.administration;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String roleName;

    //This list all the module permissions attached to this specific role

    @OneToMany(mappedBy = "role",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RoleModuleAccess> moduleAccessList;


}

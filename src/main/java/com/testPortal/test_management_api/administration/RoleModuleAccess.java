package com.testPortal.test_management_api.administration;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "role_module_access")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class RoleModuleAccess {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    //which role does this belong to
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id",nullable = false)
    private Role role;

    //which module is this for?
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id", nullable = false)
    private Module module;

    private boolean canCreate;
    private boolean canEdit;
    private boolean canDelete;
    private boolean canList;
    private boolean canView;

}

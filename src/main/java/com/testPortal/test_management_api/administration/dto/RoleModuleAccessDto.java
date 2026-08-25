package com.testPortal.test_management_api.administration.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleModuleAccessDto {
    private Integer moduleId;
    private String moduleName;
    private boolean canCreate;
    private boolean canEdit;
    private boolean canDelete;
    private boolean canList;
    private boolean canView;

}

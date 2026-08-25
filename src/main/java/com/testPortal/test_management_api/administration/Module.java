package com.testPortal.test_management_api.administration;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="modules")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Module {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false,unique = true)
    private String moduleName;

    private String moduleUrl;

    @Column(nullable = false,unique = true)
    private String moduleKey;
}

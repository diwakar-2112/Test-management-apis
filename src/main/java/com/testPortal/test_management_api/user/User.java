package com.testPortal.test_management_api.user;
import com.testPortal.test_management_api.administration.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name="users")
@Setter
@Getter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false,unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

//    @Column(nullable = false)
//    private String role;

    private String firstName;

    private String middleName;

    private String lastName;

    private String email;

    private String mobile;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status = UserStatus.ACTIVE; //default active


    // We use EAGER fetch here because every time Spring Security looks up a user,
    // it MUST immediately know their role to perform security checks!
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    private Role role;

}

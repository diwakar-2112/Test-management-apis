package com.testPortal.test_management_api.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Integer> {
    // This is a derived query method. Spring Data JPA will automatically
    // generate the SQL to find a User entity by its 'username' field.
    // We use Optional because a user with the given username might not exist.
    // This method will be critical for our login process.
    Optional<User> findByUsername(String username);
}

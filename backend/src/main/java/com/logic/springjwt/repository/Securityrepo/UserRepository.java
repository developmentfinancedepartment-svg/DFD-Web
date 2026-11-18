package com.logic.springjwt.repository.Securityrepo;

import com.logic.springjwt.models.SecurityModel.User;
import com.logic.springjwt.models.SecurityModel.UserRole;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends CrudRepository<User, String> {
    Optional<User> findUserByUsername(String username);
    Optional<User> findByUserId(String userId);
    List<User> findAllByRole(UserRole role);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}

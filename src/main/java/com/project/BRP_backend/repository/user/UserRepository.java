package com.project.BRP_backend.repository.user;

import com.project.BRP_backend.model.constants.Role;
import com.project.BRP_backend.model.user.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    User findByEmail(String email);
    Set<User> findAllByRole(Role role);
    Optional<User> findByPhoneNumber(String phoneNumber);
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByPhoneNumberAndRole(String phoneNumber, Role role);
    boolean existsByIdAndRole(String phoneNumber, Role role);



}

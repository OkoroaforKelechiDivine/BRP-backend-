package com.project.BRP_backend.repository.user;

import com.project.BRP_backend.model.user.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    User findByEmail(String email);
}

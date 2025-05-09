package com.project.BRP_backend.repository.user;

import com.project.BRP_backend.model.user.OneTimePassword;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OneTimePasswordRepository extends MongoRepository<OneTimePassword, String> {

    Optional<OneTimePassword> findByOtp(String otp);
}

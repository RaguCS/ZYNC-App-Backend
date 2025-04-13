package com.auth.authendicatorservice.Repo;

import com.auth.authendicatorservice.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByPhone(long phone);
    Boolean existsByEmail(String email);
    Boolean existsByPhone(long phone);
}


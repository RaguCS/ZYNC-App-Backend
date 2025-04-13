package com.auth.authendicatorservice.Repo;

import com.auth.authendicatorservice.Model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepo extends JpaRepository<Token,Long> {
    Boolean existsByrefreshtoken(String refreshtoken);
//    Boolean existsByEmail(String email);
    Token findByEmail(String email);
    void deleteByrefreshtoken(String refreshtoken);
}

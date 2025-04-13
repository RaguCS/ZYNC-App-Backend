package com.auth.authendicatorservice.Service;

import com.auth.authendicatorservice.Componentes.JwtUtil;
import com.auth.authendicatorservice.DTO.AuthResponceDTO;
import com.auth.authendicatorservice.DTO.RefreshTokenRequestDTO;
import com.auth.authendicatorservice.Exceptions.InvalidTokenException;
import com.auth.authendicatorservice.Model.Token;
import com.auth.authendicatorservice.Model.User;
import com.auth.authendicatorservice.Repo.RefreshTokenRepo;
import com.auth.authendicatorservice.Repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class RefreshTokenService {
    @Autowired
    private RefreshTokenRepo repository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    JwtUtil jwtUtil;

    public void storeToken(Token token) {
     Token existToken=repository.findByEmail(token.getEmail());
        if(existToken!=null&&!existToken.getRefreshtoken().equals(token.getRefreshtoken())){
            existToken.setRefreshtoken(token.getRefreshtoken());
            repository.save(existToken);
        }else {
            repository.save(token);
        }
    }

    public boolean exists(String token) {
        return repository.existsByrefreshtoken(token);
    }

    public void delete(String token) {
        repository.deleteByrefreshtoken(token);
    }
    public ResponseEntity<AuthResponceDTO> refreshToken(RefreshTokenRequestDTO request) throws InvalidTokenException {
        String refreshToken = request.refreshToken();

        if (!jwtUtil.isRefreshTokenValid(refreshToken) ||!exists(refreshToken)) {
            throw new InvalidTokenException("Invalid Refresh Token");
        } else {
            String email = jwtUtil.extractEmail(refreshToken);
            User user=userRepository.findByEmail(email).orElse(null);
            if(user==null) {
                throw new  UsernameNotFoundException("User not found");
            }

            String newAccessToken = jwtUtil.generateToken(email,user.getRole());
            String newRefreshToken = jwtUtil.generateRefreshToken(email,user.getRole());


            storeToken(new Token(email, newRefreshToken));

            return ResponseEntity.ok(new AuthResponceDTO(newAccessToken, newRefreshToken));
        }
    }
}

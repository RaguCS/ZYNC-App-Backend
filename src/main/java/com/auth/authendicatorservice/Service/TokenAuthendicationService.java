package com.auth.authendicatorservice.Service;

import com.auth.authendicatorservice.Componentes.JwtUtil;
import com.auth.authendicatorservice.DTO.AccessTokenDTO;
import com.auth.authendicatorservice.DTO.AuthResponceDTO;
import com.auth.authendicatorservice.DTO.RefreshTokenRequestDTO;
import com.auth.authendicatorservice.Exceptions.InvalidTokenException;
import com.auth.authendicatorservice.Model.Token;
import com.auth.authendicatorservice.Model.User;
import com.auth.authendicatorservice.Repo.RefreshTokenRepo;
import com.auth.authendicatorservice.Repo.UserRepository;
import com.auth.authendicatorservice.Security.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static org.springframework.http.ResponseEntity.status;

@Service
public class TokenAuthendicationService {
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private RefreshTokenRepo repository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserDetailsServiceImpl userDetailsService;


    public ResponseEntity<AuthResponceDTO> refreshToken(RefreshTokenRequestDTO request) throws InvalidTokenException {
        String refreshToken = request.refreshToken();

        if (!isValidRefreshToken(refreshToken) ||!exists(refreshToken)) {
            throw new InvalidTokenException("Invalid Refresh Token");
        } else {
            String email = jwtUtil.extractEmail(refreshToken);
            if(email == null) {
                throw new InvalidTokenException("Invalid Refresh Token");
            }
            User user=userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
            String newAccessToken = generateAccessToken(user);
            String newRefreshToken =generateRefreshToken(user);
            storeToken(new Token(email, newRefreshToken));
            return ResponseEntity.ok(new AuthResponceDTO(newAccessToken, newRefreshToken));
        }
    }
    public String generateAccessToken(User user) {
        return jwtUtil.generateToken(user.getEmail(), user.getRole());
    }
    public String generateRefreshToken(User user) {
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail(),user.getRole());
        Token token = new Token();
        token.setEmail(user.getEmail());
        token.setRefreshtoken(refreshToken);
        storeToken(token);
        return refreshToken;
    }

    public void storeToken(Token token) {
        Token existToken = repository.findByEmail(token.getEmail());

        if (existToken != null) {
            existToken.setRefreshtoken(token.getRefreshtoken());
            repository.save(existToken);
        } else {
            repository.save(token);
        }
    }

    public boolean exists(String token) {
        return repository.existsByrefreshtoken(token);
    }

    public void delete(String token) {
        repository.deleteByrefreshtoken(token);
    }
    public void deleteByMail(String mail) {
        repository.deleteByEmail(mail);
    }
    public boolean isValidRefreshToken(String refreshToken) {
        return jwtUtil.isRefreshTokenValid(refreshToken);
    }

    public ResponseEntity<AccessTokenDTO> isValidAccessToken(String accessToken) {
        if (accessToken != null && accessToken.startsWith("Bearer ")) {
            String token = accessToken.substring(7);
            String email = jwtUtil.extractEmail(token);

            if (email != null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                boolean isValid = jwtUtil.validateAccessToken(token, userDetails);

                if (isValid) {
                    return ResponseEntity.ok(new AccessTokenDTO("Valid", email, "Access token is valid."));
                }
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AccessTokenDTO("Invalid",null,"Invalid or expired token."));
    }

}

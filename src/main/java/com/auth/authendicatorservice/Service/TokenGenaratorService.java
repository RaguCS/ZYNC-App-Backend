package com.auth.authendicatorservice.Service;

import com.auth.authendicatorservice.Componentes.JwtUtil;
import com.auth.authendicatorservice.Model.Token;
import com.auth.authendicatorservice.Model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TokenGenaratorService {
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private RefreshTokenService refreshTokenService;

    public String generateAccessToken(User user) {
        return jwtUtil.generateToken(user.getEmail(), user.getRole());
    }
    public String generateRefreshToken(User user) {
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail(),user.getRole());
        Token token = new Token(user.getEmail(),refreshToken);
        refreshTokenService.storeToken(token);
        return refreshToken;
    }
}

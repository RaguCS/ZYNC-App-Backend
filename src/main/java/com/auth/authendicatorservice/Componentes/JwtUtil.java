package com.auth.authendicatorservice.Componentes;

import com.auth.authendicatorservice.Exceptions.InvalidTokenException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;

import static org.springframework.web.servlet.function.ServerResponse.status;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String SECRET;
    @Value("${jwt.refsecret}")
    private String refreshSecret;

    public String generateToken(String email, String role) {
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 2 * 60 * 1000)) // 2 mins
                .signWith(SignatureAlgorithm.HS256, SECRET)
                .compact();
    }

    public String extractEmail(String token) {
        try {
            return Jwts.parser().setSigningKey(SECRET)
                    .parseClaimsJws(token)
                    .getBody().getSubject();
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }    }

//    public boolean validateAccessToken(String token, UserDetails userDetails) {
//        return extractEmail(token).equals(userDetails.getUsername());
//    }
    public boolean validateAccessToken(String token, UserDetails userDetails) {
        final String email = extractEmail(token);
        return email.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        Date expiration = Jwts.parser().setSigningKey(SECRET)
                .parseClaimsJws(token).getBody().getExpiration();
        return expiration.before(new Date());
    }

    public String generateRefreshToken(String email,String role) {
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7)) // 7 days
                .signWith(SignatureAlgorithm.HS256, refreshSecret)
                .compact();
    }

    public boolean isRefreshTokenValid(String token) {
        try {
            Jwts.parser().setSigningKey(refreshSecret).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}

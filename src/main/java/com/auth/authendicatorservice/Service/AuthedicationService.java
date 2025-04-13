package com.auth.authendicatorservice.Service;

import com.auth.authendicatorservice.Componentes.JwtUtil;
import com.auth.authendicatorservice.DTO.*;
import com.auth.authendicatorservice.Exceptions.InvalidTokenException;
import com.auth.authendicatorservice.Exceptions.UserAlreadyExistException;
import com.auth.authendicatorservice.Model.Token;
import com.auth.authendicatorservice.Model.User;
import com.auth.authendicatorservice.Repo.UserRepository;
import com.auth.authendicatorservice.Security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;


@Service
public class AuthedicationService {
        @Autowired
        TokenGenaratorService tokenGenaratorService;
        @Autowired
        private UserRepository userRepo;
        @Autowired
        private PasswordEncoder passwordEncoder;
        @Autowired
        private EmailService emailService;
        @Autowired
        private AuthenticationManager authManager;
        @Autowired
        private RefreshTokenService refreshTokenService;
        @Autowired
        private JwtUtil jwtUtil;

        public AuthResponceDTO register(RegisterReqDTO request) throws UserAlreadyExistException {
            if(userRepo.existsByEmail(request.email())||userRepo.existsByPhone(request.phone())) {
                throw new UserAlreadyExistException("Email or Phone is already in use");
            }
            User user = new User();
            user.setName(request.name());
            user.setPhone(request.phone());
            user.setEmail(request.email());
            user.setPassword(passwordEncoder.encode(request.password()));
            user.setRole(request.role());
            user.setEnabled(false);
            OtpGenRespDTO otpGenRespDTO= emailService.sendOtpEmail(user.getEmail()).block();
            user.setOtp(otpGenRespDTO.otp());
            user.setOtpGeneratedAt(otpGenRespDTO.createdAt());
//            String accessToken = jwtUtil.generateAccessToken(user.getEmail(),user.getRole());      // Expires in 1 hour
//            String refreshToken = jwtUtil.generateRefreshToken(user.getEmail(),user.getRole());  // Expires in 7 days
//            Token token = new Token(user.getEmail(),refreshToken);
//            refreshTokenService.storeToken(token);
            userRepo.save(user);


//            return new AuthResponceDTO(accessToken, refreshToken);
            return new AuthResponceDTO("","");

        }

        public AuthResponceDTO login(LoginReqDTO request) {
            Authentication authentication=authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            User user = userDetails.getUser();
            return new AuthResponceDTO(
                    tokenGenaratorService.generateAccessToken(user),
                    tokenGenaratorService.generateRefreshToken(user)
            );
        }
        public ResponseEntity<String > verify(OtpVerifyRequestDTO request) {
            User user = userRepo.findByEmail(request.email()).orElseThrow();
            if(user.isEnabled()){
                return ResponseEntity.status(HttpStatus.ALREADY_REPORTED).body("User is Already enabled");
            }
            if (user.getOtp().equals(request.otp()) && user.getOtpGeneratedAt().isAfter(LocalDateTime.now().minusMinutes(5))) {
                user.setEnabled(true);
                user.setOtp(null);
                userRepo.save(user);
                return ResponseEntity.ok("Account verified. You can now log in.");
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired OTP.");
        }

        public String enableOrDisableUser(UserStateDTO request) {
            User user=userRepo.findByEmail(request.mail()).orElse(null);
            if (user==null) {
                throw new UsernameNotFoundException("User not found");
            }
            user.setEnabled(!user.isEnabled());
            userRepo.save(user);
            return "User State Changed"+(!user.isEnabled());
        }
        public String deleteUser(DeleteUserDTO request) throws InvalidTokenException {
            Authentication authentication=authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            User user = userDetails.getUser();
            if (!jwtUtil.isRefreshTokenValid(request.refreshToken()) ||!refreshTokenService.exists(request.refreshToken())) {
                throw new InvalidTokenException("Invalid Refresh Token");
            }
            userRepo.delete(user);
            refreshTokenService.delete(request.refreshToken());
            return "User Deleted Successfully";
        }
}



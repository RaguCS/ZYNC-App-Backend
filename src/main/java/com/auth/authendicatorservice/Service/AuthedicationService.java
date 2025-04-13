package com.auth.authendicatorservice.Service;

import com.auth.authendicatorservice.Componentes.JwtUtil;
import com.auth.authendicatorservice.DTO.*;
import com.auth.authendicatorservice.Exceptions.InvalidTokenException;
import com.auth.authendicatorservice.Exceptions.UserAlreadyExistException;
import com.auth.authendicatorservice.Model.User;
import com.auth.authendicatorservice.Repo.UserRepository;
import com.auth.authendicatorservice.Security.CustomUserDetails;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
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
        TokenAuthendicationService tokenAuthendicationService;
        @Autowired
        private UserRepository userRepo;
        @Autowired
        private PasswordEncoder passwordEncoder;
        @Autowired
        private SendOtpService sendOtpService;
        @Autowired
        private AuthenticationManager authManager;
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
            OtpGenRespDTO otpGenRespDTO= sendOtpService.sendOtpInMail(user.getEmail());
            user.setOtp(otpGenRespDTO.otp());
            user.setOtpGeneratedAt(otpGenRespDTO.createdAt());
//            String accessToken = jwtUtil.generateAccessToken(user.getEmail(),user.getRole());      // Expires in 1 hour
//            String refreshtoken = jwtUtil.generateRefreshToken(user.getEmail(),user.getRole());  // Expires in 7 days
//            Token token = new Token(user.getEmail(),refreshtoken);
//            tokenAuthendicationService.storeToken(token);
            userRepo.save(user);


//            return new AuthResponceDTO(accessToken, refreshtoken);
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
                    tokenAuthendicationService.generateAccessToken(user),
                    tokenAuthendicationService.generateRefreshToken(user)
            );
        }
        public ResponseEntity<String > verifyAccount(OtpVerifyRequestDTO request) {
              boolean isVerified=verifyOtp(request);
              User user=userRepo.findByEmail(request.email()).orElseThrow(()->new UsernameNotFoundException("Email not found"));
            if(!user.isEnabled()&&!isVerified)
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired OTP.");
            if(user.isEnabled()&&!isVerified){
                return ResponseEntity.status(HttpStatus.ALREADY_REPORTED).body("User is Already enabled");
            }
                user.setEnabled(true);
                user.setOtp(null);
                userRepo.save(user);
                return ResponseEntity.ok("Account verified. You can now log in.");

        }
        private boolean verifyOtp(OtpVerifyRequestDTO request) {
            User user=userRepo.findByEmail(request.email()).orElseThrow(() -> new UsernameNotFoundException("Email not found"));
            if(user.getOtp()!=null&&user.getOtp().equals(request.otp())&&user.getOtpGeneratedAt().isAfter(LocalDateTime.now().minusMinutes(5))) {
                return true;
            }
            return false;
        }

        public String enableOrDisableUser(UserStateDTO request) {
            User user=userRepo.findByEmail(request.mail()).orElseThrow(()->new UsernameNotFoundException("User not found"));
            user.setEnabled(!user.isEnabled());
            userRepo.save(user);
            return "User State Changed: "+(user.isEnabled());
        }
        public String deleteUserRequest(DeleteUserDTO request) throws InvalidTokenException {
            Authentication authentication=authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            User user = userDetails.getUser();
//            System.out.println(request.email());
//            User user=userRepo.findByEmail(request.email()).orElseThrow(()->new UsernameNotFoundException("Email not found"));
//            if (!user.getPassword().equals(passwordEncoder.encode(request.password()))) {
//                throw new BadCredentialsException("Invalid password");
//            }
            if (!tokenAuthendicationService.isValidRefreshToken(request.refreshtoken()) ||!tokenAuthendicationService.exists(request.refreshtoken())) {
                throw new InvalidTokenException("Invalid Refresh Token");
            }
            OtpGenRespDTO otpGenRespDTO= sendOtpService.sendOtpInMail(user.getEmail());
            user.setOtp(otpGenRespDTO.otp());
            user.setOtpGeneratedAt(otpGenRespDTO.createdAt());
            userRepo.save(user);
            return "Verify Your OTP for Deleting User";
        }
        @Transactional
        public String deleteUser(OtpVerifyRequestDTO request){
            boolean isVerified = verifyOtp(request);

            User user=userRepo.findByEmail(request.email()).orElseThrow(()->new UsernameNotFoundException("Email not found"));
            if(isVerified){
                tokenAuthendicationService.deleteByMail(user.getEmail());
                userRepo.delete(user);
                return "User Deleted Successfully";
            }
            return "Invalid or expired OTP";
        }
        public ResponseEntity<AuthResponceDTO> refreshToken(RefreshTokenRequestDTO request) throws InvalidTokenException {
            return tokenAuthendicationService.refreshToken(request);
        }

}



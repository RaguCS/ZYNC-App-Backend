package com.auth.authendicatorservice.Controller;

import com.auth.authendicatorservice.DTO.*;
import com.auth.authendicatorservice.Exceptions.InvalidTokenException;
import com.auth.authendicatorservice.Exceptions.UserAlreadyExistException;
import com.auth.authendicatorservice.Repo.UserRepository;
import com.auth.authendicatorservice.Service.AuthedicationService;
import com.auth.authendicatorservice.Service.RefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthendicationController {
    @Autowired
    AuthedicationService authedicationService;
    @Autowired
    RefreshTokenService tokenService;
    @PostMapping("/login")
    public ResponseEntity<AuthResponceDTO> login(@RequestBody LoginReqDTO request) {
     return ResponseEntity.ok(authedicationService.login(request));
    }
    @PostMapping("/changeuserstate")
    public ResponseEntity<String> enableOrDisableUser(@RequestBody UserStateDTO request) {
    return ResponseEntity.ok(authedicationService.enableOrDisableUser(request));
    }
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterReqDTO request) throws UserAlreadyExistException {
        authedicationService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
    }
    @PostMapping("/verify")
    public ResponseEntity<String> verifyOtp(@RequestBody OtpVerifyRequestDTO request) {
        return authedicationService.verify(request);
    }
    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponceDTO> refreshToken(@RequestBody RefreshTokenRequestDTO request) throws InvalidTokenException {
    return tokenService.refreshToken(request);
    }
    @PostMapping("deleteuser")
    public ResponseEntity<String> deleteUser(@RequestBody DeleteUserDTO request) throws InvalidTokenException {
        return ResponseEntity.status(HttpStatus.OK).body(authedicationService.deleteUser(request));
    }

}

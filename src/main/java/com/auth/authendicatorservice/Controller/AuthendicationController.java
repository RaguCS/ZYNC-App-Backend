package com.auth.authendicatorservice.Controller;

import com.auth.authendicatorservice.DTO.*;
import com.auth.authendicatorservice.Exceptions.InvalidTokenException;
import com.auth.authendicatorservice.Exceptions.UserAlreadyExistException;
import com.auth.authendicatorservice.Service.AuthedicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthendicationController {
    @Autowired
    AuthedicationService authedicationService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterReqDTO request) throws UserAlreadyExistException {
        authedicationService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
    }
    @PostMapping("/verify")
    public ResponseEntity<String> verifyOtp(@RequestBody OtpVerifyRequestDTO request) {
        return authedicationService.verifyAccount(request);
    }
    @PostMapping("/login")
    public ResponseEntity<AuthResponceDTO> login(@RequestBody LoginReqDTO request) {
     return ResponseEntity.ok(authedicationService.login(request));
    }
    @PostMapping("/change-user-state")
    public ResponseEntity<String> enableOrDisableUser(@RequestBody UserStateDTO request) {
    return ResponseEntity.status(HttpStatus.OK).body(authedicationService.enableOrDisableUser(request));
    }
    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponceDTO> refreshToken(@RequestBody RefreshTokenRequestDTO request) throws InvalidTokenException {
    return authedicationService.refreshToken(request);
    }
    @PostMapping("/delete-user-req")
    public ResponseEntity<String> deleteUserReq(@RequestBody DeleteUserDTO request) throws InvalidTokenException {
        return ResponseEntity.status(HttpStatus.OK).body(authedicationService.deleteUserRequest(request));
    }
    @DeleteMapping("/delete-user")
    public ResponseEntity<String> deleteUser(@RequestBody OtpVerifyRequestDTO request){
        return ResponseEntity.ok(authedicationService.deleteUser(request));
    }

}

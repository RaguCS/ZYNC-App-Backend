package com.auth.authendicatorservice.DTO;

public record OtpVerifyRequestDTO(String email, String password, String otp) {
}

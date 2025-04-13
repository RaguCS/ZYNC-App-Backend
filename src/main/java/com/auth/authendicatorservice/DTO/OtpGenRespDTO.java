package com.auth.authendicatorservice.DTO;

import java.time.LocalDateTime;

public record OtpGenRespDTO (String email, String otp, LocalDateTime createdAt) {
}

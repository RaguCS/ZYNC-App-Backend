package com.auth.authendicatorservice.Service;

import com.auth.authendicatorservice.DTO.OtpGenRespDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SendOtpService {
    @Autowired
    EmailService emailService;

    public OtpGenRespDTO sendOtpInMail(String email) {
        OtpGenRespDTO otpGenRespDTO=emailService.sendOtpEmail(email).block();
        return otpGenRespDTO;
    }
}

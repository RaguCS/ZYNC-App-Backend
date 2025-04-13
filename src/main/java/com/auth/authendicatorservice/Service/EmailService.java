package com.auth.authendicatorservice.Service;

import com.auth.authendicatorservice.DTO.OtpGenReqDTO;
import com.auth.authendicatorservice.DTO.OtpGenRespDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class EmailService {
    @Autowired
    WebClient.Builder webClientBuilder;

    public Mono<OtpGenRespDTO> sendOtpEmail(String to) {
        return webClientBuilder
                .baseUrl("http://otp-service/otp")
                .build()
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path("/sendmail")
                        .queryParam("email", to)
                        .build())
                .retrieve()
                .bodyToMono(OtpGenRespDTO.class);
    }

//    @Autowired
//    WebClient webClient;

//    public Mono<OtpGenRespDTO> sendOtpEmail(String to) {
//        OtpGenReqDTO otpGenReqDTO=new OtpGenReqDTO(to);
//        return  webClient.post()
//                .uri(uriBuilder -> uriBuilder
//                        .path("/sendmail")
//                        .queryParam("email", otpGenReqDTO.email())
//                        .build())
//                .retrieve()
//                .bodyToMono(OtpGenRespDTO.class);
//    }
}


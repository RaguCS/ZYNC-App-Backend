package com.auth.authendicatorservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class AuthendicatorServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthendicatorServiceApplication.class, args);
    }

}

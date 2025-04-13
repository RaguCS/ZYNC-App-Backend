package com.auth.authendicatorservice.DTO;

public record RegisterReqDTO(String name, long phone,String email, String password, String role) {
}

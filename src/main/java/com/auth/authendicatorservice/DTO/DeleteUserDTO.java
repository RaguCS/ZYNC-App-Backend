package com.auth.authendicatorservice.DTO;

public record DeleteUserDTO(String email, long phone, String password, String accesstoken, String refreshtoken) {
}

package com.auth.authendicatorservice.DTO;

public record DeleteUserDTO(String email, long phone, String password,  String currenttoken,String refreshtoken) {
}

package com.auth.authendicatorservice.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "Users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "uid")
    private Long id;
    @Column(name = "uname")
    private String name;
    @Column(name = "uemail", unique = true, nullable = false)
    private String email;
    @Column(name = "uphoneno", nullable = false, unique = true)
    private long phone;
    @Column(name = "upassword")
    private String password;
    @Column(name = "urole")
    private String role;
    @Column(name = "uenable")
    private boolean enabled;
    @Column(name = "uotp")
    private String otp;
    @Column(name = "uotpGeneratedAt")
    private LocalDateTime otpGeneratedAt;


}

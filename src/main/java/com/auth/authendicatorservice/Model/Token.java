package com.auth.authendicatorservice.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "RefreshTokens")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Token {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "tokId")
        Long id;

        @Column(unique = true, nullable = false)
        String email;
        @Column(unique = true, nullable = false)
        String refreshtoken;
        public Token(String email, String refreshtoken) {
            this.email = email;
            this.refreshtoken = refreshtoken;
        }
}

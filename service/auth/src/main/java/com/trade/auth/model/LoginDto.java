package com.trade.auth.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class LoginDto {
    @Getter
    @Setter
    public static class Request {
        @Email
        @NotBlank
        private String email;

        @NotBlank
        private String password;
    }

    @Getter
    @Builder
    public static class Response {
        private String userId;
        private String email;
        private String name;
        private String role;

        private String accessToken;
        private long expiresIn;
    }
}
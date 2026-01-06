package com.trade.auth.model;

import com.trade.auth.entity.AuthUsers;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


public class SignupDto {

    @Getter
    @Setter
    public static class Request {
        private String email;
        private String password;
        private String name;

    }

    @Getter
    @Setter
    @Builder
    public static class Response {
        private AuthUsers user;
    }
}

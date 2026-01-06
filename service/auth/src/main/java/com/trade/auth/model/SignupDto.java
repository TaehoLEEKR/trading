package com.trade.auth.model;

import com.trade.auth.entity.AuthUsers;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;


public class SignupDto {

    @Getter
    @Setter
    public static class Request {

        @NotBlank(message = "email은 필수입니다.")
        @Email(message = "email 형식이 올바르지 않습니다.")
        @Size(max = 255, message = "email은 255자 이하여야 합니다.")
        private String email;

        @NotBlank(message = "password는 필수입니다.")
        @Size(min = 8, max = 72, message = "password는 8~72자여야 합니다.")
        private String password;

        @Size(max = 100, message = "name은 100자 이하여야 합니다.")
        private String name;

    }

    @Getter
    @Setter
    @Builder
    public static class Response {
        private AuthUsers user;
    }
}

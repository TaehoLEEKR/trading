package com.trade.auth.service;

import com.trade.auth.entity.AuthUsers;
import com.trade.auth.model.SignupDto;
import com.trade.common.constant.ErrorCode;
import com.trade.common.exception.CustomException;
import com.trade.common.util.AES256;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SignupService {

    @Value("${aes.key}")
    private String EncryptKey;

    public SignupDto.Response signup(SignupDto.Request request) {
        try {

            String uuidUser = UUID.randomUUID().toString().substring(0,36);

            AuthUsers authUsers = AuthUsers.builder()
                    .user_id(uuidUser)
                    .email(request.getEmail())
                    .name(request.getName())
                    .passwordHash(AES256.Encrypt(request.getPassword(), EncryptKey))
                    .build();

            // DB 적재

            // 적재 확인
            return SignupDto.Response.builder()
                    .user(authUsers)
                    .build();

        }catch (Exception e) {
            log.error("회원가입 실패 : {} ", e.getMessage());
            throw new CustomException(ErrorCode.SERVER_ERROR);
        }
    }
}

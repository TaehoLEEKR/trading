package com.trade.auth.service;

import com.trade.auth.entity.AuthUsers;
import com.trade.auth.model.SignupDto;
import com.trade.auth.repository.AuthUsersRepository;
import com.trade.common.constant.ErrorCode;
import com.trade.common.exception.CustomException;
import com.trade.common.util.AES256;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.trade.common.constant.staticConst.USER;

@Service
@RequiredArgsConstructor
@Slf4j
public class SignupService {

    @Value("${aes.key}")
    private String EncryptKey;
    private final AuthUsersRepository authUsersRepository;

    @Transactional
    public SignupDto.Response signup(SignupDto.Request request) {
        try {

            String uuidUser = UUID.randomUUID().toString().substring(0,36);

            AuthUsers authUsers = AuthUsers.builder()
                    .userId(uuidUser)
                    .email(request.getEmail())
                    .name(request.getName())
                    .role(USER)
                    .passwordHash(AES256.Encrypt(request.getPassword(), EncryptKey))
                    .build();

            // DB 적재
            if(!isEmailExists(request.getEmail())) {
                authUsersRepository.save(authUsers);
            }else{
                throw new CustomException(ErrorCode.CONFLICT_DATA);
            }
            // 적재 확인
            return SignupDto.Response.builder()
                    .userId(authUsers.getUserId())
                    .email(authUsers.getEmail())
                    .name(authUsers.getName())
                    .build();

        }
        catch (CustomException e) {
            throw e;
        }
        catch (Exception e) {
            log.error("회원가입 실패 : {} ", e.getMessage());
            throw new CustomException(ErrorCode.SERVER_ERROR);
        }
    }

    @Transactional(readOnly = true)
    public boolean isEmailExists(String email) {
        return authUsersRepository.existsByEmail(email);
    }
}

package com.trade.auth.service;

import com.trade.auth.component.JwtProvider;
import com.trade.auth.component.RefreshTokenStore;
import com.trade.auth.component.TokenGenerator;
import com.trade.auth.entity.AuthUsers;
import com.trade.auth.model.LoginDto;
import com.trade.auth.model.SignupDto;
import com.trade.auth.record.LoginResult;
import com.trade.auth.repository.AuthUsersRepository;
import com.trade.common.constant.ErrorCode;
import com.trade.common.exception.CustomException;
import com.trade.common.util.AES256;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.UUID;

import static com.trade.common.constant.staticConst.USER;

@Service
@RequiredArgsConstructor
@Slf4j
public class SignupService {

    private static final Duration REFRESH_TOKEN_TTL = Duration.ofDays(14);

    @Value("${aes.key}")
    private String EncryptKey;
    @Value("${jwt.access-token-validity-seconds:900}")
    private long accessTokenValiditySeconds;

    private final AuthUsersRepository authUsersRepository;
    private final RefreshTokenStore refreshTokenStore;
    private final JwtProvider jwtProvider;
    private final TokenGenerator tokenGenerator;

    @Transactional
    public SignupDto.Response signup(SignupDto.Request request) {
        try {

            String uuidUser = UUID.randomUUID().toString().substring(0,36);

            AuthUsers authUsers = AuthUsers.builder()
                    .userId(uuidUser)
                    .email(request.getEmail())
                    .name(request.getName())
                    .role(USER)
                    .passwordHash(AES256.Encrypt(EncryptKey, request.getPassword()))
                    .build();

            // DB 검증
            if(!isEmailExists(request.getEmail())) {
                authUsersRepository.save(authUsers);
            }else{
                throw new CustomException(ErrorCode.CONFLICT_DATA);
            }

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

    public LoginResult login(LoginDto.Request request) {

        AuthUsers user = authUsersRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.VALIDATION_ERROR));

        //pw 검증
        if(!AES256.matches(EncryptKey, request.getPassword(), user.getPasswordHash())){
            throw new CustomException(ErrorCode.VALIDATION_PW_ERROR);
        }

        String accessToken = getAccessToken(user);

        String refreshToken = tokenGenerator.randomBase64Url(32);


        refreshTokenStore.store(refreshToken, user.getUserId(), REFRESH_TOKEN_TTL);


        LoginDto.Response body = LoginDto.Response.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole())
                .accessToken(accessToken)
                .expiresIn(accessTokenValiditySeconds)
                .build();

        return new LoginResult(body, refreshToken);
    }

    public LoginResult refresh(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        String userId = refreshTokenStore.getUserId(refreshToken);
        if (userId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        AuthUsers user = authUsersRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));

        refreshTokenStore.revoke(refreshToken);

//        String accessToken = jwtProvider.issueAccessToken(user.getUserId(), user.getRole());
        String accessToken = getAccessToken(user);
        String newRefreshToken = tokenGenerator.randomBase64Url(32);
        refreshTokenStore.store(newRefreshToken, user.getUserId(), REFRESH_TOKEN_TTL);

        LoginDto.Response body = LoginDto.Response.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole())
                .accessToken(accessToken)
                .expiresIn(accessTokenValiditySeconds)
                .build();

        return new LoginResult(body, newRefreshToken);
    }
    @Transactional(readOnly = true)
    public boolean isEmailExists(String email) {
        return authUsersRepository.existsByEmail(email);
    }

    public String getAccessToken(AuthUsers user) {
        return jwtProvider.issueAccessToken(user.getUserId(), user.getRole());
    }

}





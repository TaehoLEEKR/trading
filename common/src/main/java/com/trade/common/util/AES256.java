package com.trade.common.util;

import lombok.experimental.UtilityClass;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;



@UtilityClass
public class AES256 {

    private static final String ALGO = "AES";
    // GCM 은 스트림 처럼 동작 하여 Padding 이 불필요함
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int GCM_TAG_LENGTH_BITS = 128;
    private static final int IV_LENGTH_BYTES = 12;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public String Encrypt(String key, String value) {

        if (value == null) return null;

        try {
            byte[] iv = new byte[IV_LENGTH_BYTES];
            SECURE_RANDOM.nextBytes(iv);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);

            cipher.init(
                    Cipher.ENCRYPT_MODE,
                    new SecretKeySpec(normalizeKeyTo32Bytes(key), ALGO),
                    new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv)
            );

            byte[] cipherBytes = cipher.doFinal(value.getBytes(StandardCharsets.UTF_8));

            byte[] out = new byte[iv.length + cipherBytes.length];
            System.arraycopy(iv, 0, out, 0, iv.length);
            System.arraycopy(cipherBytes, 0, out, iv.length, cipherBytes.length);

            return Base64.getEncoder().encodeToString(out);
        } catch (Exception e) {
            throw new IllegalStateException("AES256 Encrypt 실패", e);
        }
    }

    public String Decrypt(String key, String value) {
        if (value == null) return null;
        try {
            byte[] all = Base64.getDecoder().decode(value);
            if (all.length <= IV_LENGTH_BYTES) {
                throw new IllegalArgumentException("암호문이 유효하지 않습니다.(IV 길이 부족)");
            }

            byte[] iv = new byte[IV_LENGTH_BYTES];
            byte[] cipherBytes = new byte[all.length - IV_LENGTH_BYTES];
            System.arraycopy(all, 0, iv, 0, IV_LENGTH_BYTES);
            System.arraycopy(all, IV_LENGTH_BYTES, cipherBytes, 0, cipherBytes.length);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(
                    Cipher.DECRYPT_MODE,
                    new SecretKeySpec(normalizeKeyTo32Bytes(key), ALGO),
                    new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv)
            );

            byte[] plain = cipher.doFinal(cipherBytes);
            return new String(plain, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException("AES256 Decrypt 실패", e);
        }
    }

    private static byte[] normalizeKeyTo32Bytes(String key) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("AES key가 비어있습니다.");
        }

        // Base64로 32바이트 키가 들어오는 경우 그대로 사용
        try {
            byte[] decoded = Base64.getDecoder().decode(key);
            if (decoded.length == 32) return decoded;
        } catch (IllegalArgumentException ignored) {
            // not base64
        }

        // 그 외에는 SHA-256으로 32바이트 키 생성
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            return sha256.digest(key.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new IllegalStateException("AES key 정규화 실패", e);
        }
    }
}
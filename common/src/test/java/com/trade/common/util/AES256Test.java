package com.trade.common.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class AES256Test {

    @Test
    public void testEncryptDecrypt() {
        String key = "testKey";
        String value = "testValue";

        String encrypted = AES256.Encrypt(key, value);
        assertNotNull(encrypted);

        String decrypted = AES256.Decrypt(key, encrypted);
        assertEquals(value, decrypted);
    }
}
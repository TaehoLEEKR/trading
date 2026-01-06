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

    @Test
    void testMatchesReturnsTrueForSamePlainText() {
        String key = "testKey";
        String value = "testValue";

        String encrypted = AES256.Encrypt(key, value);
        assertTrue(AES256.matches(key, value, encrypted));
    }

    @Test
    void testMatchesReturnsFalseForDifferentPlainText() {
        String key = "testKey";
        String value = "testValue";

        String encrypted = AES256.Encrypt(key, value);
        assertFalse(AES256.matches(key, "wrongValue", encrypted));
    }
}

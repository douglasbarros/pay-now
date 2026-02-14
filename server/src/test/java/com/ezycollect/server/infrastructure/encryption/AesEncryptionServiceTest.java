package com.ezycollect.server.infrastructure.encryption;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AesEncryptionServiceTest {

    private AesEncryptionService encryptionService;

    @BeforeEach
    void setUp() {
        encryptionService = new AesEncryptionService("MySecureSecret16");
    }

    @Test
    void shouldEncryptAndDecryptSuccessfully() {
        String plainText = "4532015112830366";

        String encrypted = encryptionService.encrypt(plainText);
        String decrypted = encryptionService.decrypt(encrypted);

        assertNotEquals(plainText, encrypted);
        assertEquals(plainText, decrypted);
    }

    @Test
    void shouldProduceDifferentEncryptionWithDifferentKeys() {
        AesEncryptionService service1 = new AesEncryptionService("MySecureSecret16");
        AesEncryptionService service2 = new AesEncryptionService("DifferentKey1234");

        String plainText = "4532015112830366";
        String encrypted1 = service1.encrypt(plainText);
        String encrypted2 = service2.encrypt(plainText);

        assertNotEquals(encrypted1, encrypted2);
    }

    @Test
    void shouldHandleEmptyString() {
        String plainText = "";

        String encrypted = encryptionService.encrypt(plainText);
        String decrypted = encryptionService.decrypt(encrypted);

        assertEquals(plainText, decrypted);
    }

    @Test
    void shouldHandleSpecialCharacters() {
        String plainText = "Test@123!#$%^&*()";

        String encrypted = encryptionService.encrypt(plainText);
        String decrypted = encryptionService.decrypt(encrypted);

        assertEquals(plainText, decrypted);
    }

    @Test
    void shouldHandleLongStrings() {
        String plainText = "This is a very long string with lots of characters to test encryption and decryption";

        String encrypted = encryptionService.encrypt(plainText);
        String decrypted = encryptionService.decrypt(encrypted);

        assertEquals(plainText, decrypted);
    }
}

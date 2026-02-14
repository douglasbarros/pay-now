package com.ezycollect.server.infrastructure.encryption;

import com.ezycollect.server.domain.service.EncryptionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Implementation of EncryptionService using AES encryption.
 */
@Service
public class AesEncryptionService implements EncryptionService {

    private static final String ALGORITHM = "AES";
    private final SecretKeySpec secretKey;

    public AesEncryptionService(@Value("${encryption.secret-key}") String secretKeyString) {
        // Ensure the key is 16 bytes (128 bits) for AES
        byte[] key = adjustKeyLength(secretKeyString.getBytes(StandardCharsets.UTF_8), 16);
        this.secretKey = new SecretKeySpec(key, ALGORITHM);
    }

    @Override
    public String encrypt(String plainText) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error encrypting data", e);
        }
    }

    @Override
    public String decrypt(String encryptedText) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decodedBytes = Base64.getDecoder().decode(encryptedText);
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Error decrypting data", e);
        }
    }

    /**
     * Adjusts key length to match AES requirements.
     */
    private byte[] adjustKeyLength(byte[] key, int length) {
        byte[] adjustedKey = new byte[length];
        if (key.length >= length) {
            System.arraycopy(key, 0, adjustedKey, 0, length);
        } else {
            System.arraycopy(key, 0, adjustedKey, 0, key.length);
            // Fill remaining with zeros
            for (int i = key.length; i < length; i++) {
                adjustedKey[i] = 0;
            }
        }
        return adjustedKey;
    }
}

package com.ezycollect.server.domain.service;

/**
 * Port (interface) for encryption operations.
 * This defines the contract that infrastructure adapters must implement.
 */
public interface EncryptionService {

    /**
     * Encrypts sensitive data.
     *
     * @param plainText the plain text to encrypt
     * @return the encrypted text
     */
    String encrypt(String plainText);

    /**
     * Decrypts encrypted data.
     *
     * @param encryptedText the encrypted text to decrypt
     * @return the decrypted plain text
     */
    String decrypt(String encryptedText);
}

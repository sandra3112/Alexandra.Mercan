package com.ecommerce.service;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureMockMvc
class EncryptionServiceTest {

    @Autowired
    private EncryptionService encryptionService;

    @Test
    void passwordEncryption() {
	String password = "PasswordIsASecret!123";
	String hash = encryptionService.encryptPassword(password);
	assertTrue(encryptionService.verifyPassword(password, hash), "Hashed password should match original.");
	assertFalse(encryptionService.verifyPassword(password + "Sike!", hash), "Altered password should not be valid.");
    }
}

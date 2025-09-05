package com.unibs.tests;

import com.unibs.services.PasswordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
class PasswordServiceTest {

    private PasswordService passwordService;

    @BeforeEach
    void setUp() {
        passwordService = new PasswordService();
    }

    @Test
    void testDeterministicHashWithSameSalt() {
        byte[] salt = passwordService.generateSalt();
        String password = "TestPassword123!";
        String hash1 = passwordService.hashPassword(password,salt);
        String hash2 = passwordService.hashPassword(password,salt);
        assertEquals(hash1, hash2);
    }

    @Test
    void testDifferentSaltProducesDifferentHash() {
        String password = "TestPassword123!";
        byte[] salt1 = passwordService.generateSalt();
        byte[] salt2 = passwordService.generateSalt();
        String hash1 = passwordService.hashPassword(password,salt1);
        String hash2 = passwordService.hashPassword(password,salt2);
        assertNotEquals(hash1, hash2);
    }

    @Test
    void testHashLengthIsCorrect() {
        String password = "TestPassword123!";
        byte[] salt = passwordService.generateSalt();
        String hash = passwordService.hashPassword(password,salt);
        assertEquals(64, hash.length());
    }

    @Test
    void testHashNotNullOrEmpty() {
        String password = "TestPassword123!";
        byte[] salt = passwordService.generateSalt();
        String hash = passwordService.hashPassword(password,salt);
        assertNotNull(hash);
        assertFalse(hash.isEmpty());
    }

    @Test
    void testHashContainsHexCharactersOnly() {
        String password = "TestPassword123!";
        byte[] salt = passwordService.generateSalt();
        String hash = passwordService.hashPassword(password,salt);
        assertTrue(hash.matches("[0-9a-f]+"));
    }

    @Test
    void testNullPasswordThrowsException() {
        byte[] salt = passwordService.generateSalt();
        assertThrows(NullPointerException.class, () -> passwordService.hashPassword(null,salt));
    }

    @Test
    void testDifferentPasswordsSameSalt(){
        String password1 = "TestPassword123!";
        String password2 = "TestPassword132!";
        String password3 = "TestPassword321!";
        String password4 = "TestPassword123@";

        byte[] salt = passwordService.generateSalt();
        String hash1 = passwordService.hashPassword(password1,salt);
        String hash2 = passwordService.hashPassword(password2,salt);
        String hash3 = passwordService.hashPassword(password3,salt);
        String hash4 = passwordService.hashPassword(password4,salt);

        assertNotEquals(hash1, hash2);
        assertNotEquals(hash1, hash3);
        assertNotEquals(hash1, hash4);
        assertNotEquals(hash2, hash3);
        assertNotEquals(hash2, hash4);
        assertNotEquals(hash3, hash4);
    }

    @Test
    void testSaltUniqueness() {
        byte[] salt1 = passwordService.generateSalt();
        byte[] salt2 = passwordService.generateSalt();

        assertNotNull(salt1);
        assertNotNull(salt2);
        assertEquals(16, salt1.length);
        assertEquals(16, salt2.length);
        assertNotEquals(salt1, salt2);
    }

    @Test
    void testSaltEntropy() {
        byte[] salt = passwordService.generateSalt();

        //Test che il salt non sia tutto zero
        boolean hasNonZero = false;
        for (byte b : salt) {
            if (b != 0) {
                hasNonZero = true;
                break;
            }
        }
        assertTrue(hasNonZero);

        //Test distribuzione bit (almeno alcuni bit dovrebbero essere 1 e alcuni 0)
        int setBits = 0;
        for (byte b : salt) {
            setBits += Integer.bitCount(b & 0xFF);
        }
        assertTrue(setBits > 20 && setBits < 108);
    }

    @Test
    void testCaseSensitivity() {
        byte[] salt = passwordService.generateSalt();
        String lowerHash = passwordService.hashPassword("password",salt);
        String upperHash = passwordService.hashPassword("PASSWORD",salt);
        String mixedHash = passwordService.hashPassword("PassWord",salt);

        assertNotEquals(lowerHash, upperHash);
        assertNotEquals(lowerHash, mixedHash);
        assertNotEquals(mixedHash, upperHash);
    }

    @Test
    void testNullSaltThrowsException() {
        assertThrows(NullPointerException.class, () -> passwordService.hashPassword("TestPassword123!", null));
    }

    @Test
    void testAvalangeEffect(){
        byte[] salt = passwordService.generateSalt();
        String hash1 = passwordService.hashPassword("TestPassword123!",salt);
        String hash2 = passwordService.hashPassword("TestPassword123@",salt);
        assertNotEquals(hash1, hash2);

        // Conta quanti caratteri sono diversi (dovrebbero essere molti per SHA-256)
        int differentChars = 0;
        for(int i = 0; i < hash1.length(); i++){
            if(hash1.charAt(i) != hash2.charAt(i)){
                differentChars++;
            }
        }

        // Con SHA-256, anche un piccolo cambio dovrebbe modificare circa il 50% dei bit
        assertTrue(differentChars > 20);
    }
}
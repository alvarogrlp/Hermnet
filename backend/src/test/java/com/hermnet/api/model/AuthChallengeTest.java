package com.hermnet.api.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for AuthChallenge entity.
 * 
 * Verifies builder functionality, expiration logic, and relationships.
 */
public class AuthChallengeTest {

    @Test
    public void testBuilder() {
        // Given
        User user = new User();
        user.setId("test-user");
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(5);
        String challengeStr = "random-nonce-123";

        // When
        AuthChallenge challenge = AuthChallenge.builder()
                .challenge(challengeStr)
                .user(user)
                .expiresAt(expiry)
                .build();

        // Then
        assertEquals(challengeStr, challenge.getChallenge());
        assertEquals(user, challenge.getUser());
        assertEquals(expiry, challenge.getExpiresAt());
    }

    @Test
    public void testIsExpired_WhenExpired_ShouldReturnTrue() {
        // Given
        AuthChallenge challenge = new AuthChallenge();
        challenge.setExpiresAt(LocalDateTime.now().minusSeconds(1)); // Expired 1 second ago

        // When
        boolean isExpired = challenge.isExpired();

        // Then
        assertTrue(isExpired, "Challenge should be expired");
    }

    @Test
    public void testIsExpired_WhenNotExpired_ShouldReturnFalse() {
        // Given
        AuthChallenge challenge = new AuthChallenge();
        challenge.setExpiresAt(LocalDateTime.now().plusSeconds(60)); // Expires in 60 seconds

        // When
        boolean isExpired = challenge.isExpired();

        // Then
        assertFalse(isExpired, "Challenge should not be expired yet");
    }

    @Test
    public void testNoArgsConstructor() {
        // When
        AuthChallenge challenge = new AuthChallenge();

        // Then
        assertNotNull(challenge);
        assertNull(challenge.getChallenge());
    }

    @Test
    public void testAllArgsConstructor() {
        // Given
        User user = new User();
        LocalDateTime now = LocalDateTime.now();

        // When
        AuthChallenge challenge = new AuthChallenge("nonce", user, now);

        // Then
        assertEquals("nonce", challenge.getChallenge());
        assertEquals(user, challenge.getUser());
        assertEquals(now, challenge.getExpiresAt());
    }
}

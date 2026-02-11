package com.hermnet.api.repository;

import com.hermnet.api.model.AuthChallenge;
import com.hermnet.api.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for AuthChallengeRepository.
 * 
 * Verifies storing, retrieving, deleting challenges, and custom query methods.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AuthChallengeRepositoryTest {

    @Autowired
    private AuthChallengeRepository challengeRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    public void setUp() {
        challengeRepository.deleteAll();
        userRepository.deleteAll();

        // Create a test user
        testUser = User.builder()
                .id("HNET-AUTH-USER")
                .publicKey("some-auth-key")
                .build();
        userRepository.save(testUser);
    }

    @Test
    public void testSaveAndFindChallenge() {
        // Given
        String nonce = "random-nonce-123";
        AuthChallenge challenge = AuthChallenge.builder()
                .challenge(nonce)
                .user(testUser)
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .build();

        // When
        challengeRepository.save(challenge);

        // Then
        Optional<AuthChallenge> found = challengeRepository.findById(nonce);
        assertTrue(found.isPresent(), "Should find saved challenge");
        assertEquals(nonce, found.get().getChallenge());
        assertEquals(testUser.getId(), found.get().getUser().getId());
    }

    @Test
    public void testDeleteByUser() {
        // Given - Create multiple challenges for the same user
        AuthChallenge c1 = new AuthChallenge("nonce1", testUser, LocalDateTime.now().plusMinutes(5));
        AuthChallenge c2 = new AuthChallenge("nonce2", testUser, LocalDateTime.now().plusMinutes(10));
        challengeRepository.saveAll(List.of(c1, c2));

        // And create a challenge for another user to ensure it's not deleted
        User otherUser = userRepository.save(User.builder().id("HNET-OTHER").publicKey("other-key").build());
        AuthChallenge c3 = new AuthChallenge("nonce3", otherUser, LocalDateTime.now().plusMinutes(5));
        challengeRepository.save(c3);

        assertEquals(3, challengeRepository.count(), "Should have 3 challenges initially");

        // When - Delete challenges for the first user
        challengeRepository.deleteByUser(testUser);

        // Then
        assertEquals(1, challengeRepository.count(), "Should have 1 challenge remaining");
        assertTrue(challengeRepository.existsById("nonce3"), "Other user's challenge should persist");
        assertFalse(challengeRepository.existsById("nonce1"), "User's challenge should be deleted");
        assertFalse(challengeRepository.existsById("nonce2"), "User's challenge should be deleted");
    }

    @Test
    public void testDeleteByUser_WhenNoChallengesExist_ShouldNotThrowException() {
        // When/Then
        assertDoesNotThrow(() -> challengeRepository.deleteByUser(testUser));
    }

    @Test
    public void testExpiredChallengePersistence() {
        // Given - Save an expired challenge
        String expiredNonce = "expired-nonce";
        AuthChallenge expired = new AuthChallenge(
                expiredNonce,
                testUser,
                LocalDateTime.now().minusMinutes(1));
        challengeRepository.save(expired);

        // When
        Optional<AuthChallenge> found = challengeRepository.findById(expiredNonce);

        // Then
        assertTrue(found.isPresent());
        assertTrue(found.get().isExpired(), "Retrieved challenge should report itself as expired");
    }
}

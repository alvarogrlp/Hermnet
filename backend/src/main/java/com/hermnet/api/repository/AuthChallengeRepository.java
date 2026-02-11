package com.hermnet.api.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hermnet.api.model.AuthChallenge;
import com.hermnet.api.model.User;

/**
 * Repository interface for AuthChallenge entity operations.
 * 
 * Provides methods to store, retrieve, and delete authentication challenges
 * (nonces).
 */
public interface AuthChallengeRepository extends JpaRepository<AuthChallenge, String> {

    Optional<AuthChallenge> findByNonce(String nonce);
    /**
     * Deletes all challenges associated with a specific user.
     * 
     * This is typically used to clean up old or unused challenges after a
     * successful login
     * or when a user session expires to maintain security hygiene.
     * 
     * @param user The user whose challenges should be deleted
     */
    void deleteByUser(User user);

    void deleteByExpiresAtBefore(LocalDateTime now);
}

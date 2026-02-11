package com.hermnet.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hermnet.api.model.Message;

/**
 * Repository interface for Message entity database operations.
 * 
 * Provides methods to store and retrieve secure, end-to-end encrypted messages.
 */
public interface MessageRepository extends JpaRepository<Message, Long> {

    /**
     * Retrieves all messages for a specific recipient, ordered by creation time
     * (newest first).
     * 
     * This method is used when a client polls for new messages. The ordering
     * ensures
     * that the most recent communications appear at the top of the list.
     * 
     * @param recipientId The ID of the user to retrieve messages for
     * @return A list of messages for the recipient, ordered by createdAt descending
     */
    List<Message> findByRecipientIdOrderByCreatedAtDesc(String recipientId);
}

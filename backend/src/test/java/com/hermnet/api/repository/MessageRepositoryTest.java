package com.hermnet.api.repository;

import com.hermnet.api.model.Message;
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
 * Integration tests for MessageRepository.
 * 
 * Verifies storing, retrieving encrypted messages, and custom ordering by
 * creation time.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class MessageRepositoryTest {

    @Autowired
    private MessageRepository messageRepository;

    private static final String RECIPIENT_ID = "HNET-TEST-RECIPIENT";
    private static final String SENDER_HASH = "mock-sender-hash";
    private static final byte[] DATA = new byte[] { 1, 2, 3 };

    @BeforeEach
    public void setUp() {
        messageRepository.deleteAll();
    }

    @Test
    public void testSaveAndRetrieveMessage() {
        // Given
        Message msg = Message.builder()
                .recipientId(RECIPIENT_ID)
                .senderIdHash(SENDER_HASH)
                .encryptedImage(DATA)
                .createdAt(LocalDateTime.now()) // PrePersist sets it, but builder allows override
                .build();

        // When
        messageRepository.save(msg);

        // Then
        List<Message> allMessages = messageRepository.findAll();
        assertEquals(1, allMessages.size());
        assertEquals(RECIPIENT_ID, allMessages.get(0).getRecipientId());
        assertArrayEquals(DATA, allMessages.get(0).getEncryptedImage());
    }

    @Test
    public void testFindByRecipientIdOrderedByCreatedAtDesc() {
        // Given - Create messages at different times
        Message oldMsg = Message.builder()
                .recipientId(RECIPIENT_ID)
                .senderIdHash("hash1")
                .encryptedImage(new byte[] { 1 })
                .createdAt(LocalDateTime.now().minusHours(1))
                .build();
        messageRepository.save(oldMsg);

        Message newMsg = Message.builder()
                .recipientId(RECIPIENT_ID)
                .senderIdHash("hash2")
                .encryptedImage(new byte[] { 2 })
                .createdAt(LocalDateTime.now())
                .build();
        messageRepository.save(newMsg);

        // And create a message for another user
        messageRepository.save(Message.builder()
                .recipientId("HNET-OTHER-USER")
                .senderIdHash("hash3")
                .encryptedImage(new byte[] { 3 })
                .createdAt(LocalDateTime.now())
                .build());

        // When - Find for our recipient
        List<Message> found = messageRepository.findByRecipientIdOrderByCreatedAtDesc(RECIPIENT_ID);

        // Then
        assertEquals(2, found.size(), "Should find 2 messages for this recipient");

        // Check order (newest first)
        assertEquals(newMsg.getSenderIdHash(), found.get(0).getSenderIdHash(), "Newest message should be first");
        assertEquals(oldMsg.getSenderIdHash(), found.get(1).getSenderIdHash(), "Older message should be second");

        assertTrue(found.get(0).getCreatedAt().isAfter(found.get(1).getCreatedAt()), "Timestamps should be descending");
    }

    @Test
    public void testFindByRecipientId_WhenNoMessages_ShouldReturnEmptyList() {
        // When
        List<Message> found = messageRepository.findByRecipientIdOrderByCreatedAtDesc("NON-EXISTENT");

        // Then
        assertTrue(found.isEmpty(), "Should return empty list for user with no messages");
    }

    @Test
    public void testSaveWithoutId_ShouldAutoGenerateId() {
        // Given
        Message msg = Message.builder()
                .recipientId("HNET-AUTO-ID")
                .senderIdHash("hash")
                .encryptedImage(DATA)
                // createdAt will be set by @PrePersist
                .build();

        assertNull(msg.getId());

        // When
        Message saved = messageRepository.save(msg);

        // Then
        assertNotNull(saved.getId(), "ID should be auto-generated");
        assertNotNull(saved.getCreatedAt(), "CreatedAt should be auto-generated");
    }
}

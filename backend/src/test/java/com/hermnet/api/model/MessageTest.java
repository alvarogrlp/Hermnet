package com.hermnet.api.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Message entity.
 * 
 * Verifies builder functionality, lifecycle callbacks, and edge cases.
 */
public class MessageTest {

    @Test
    public void testBuilder() {
        // Given
        String recipient = "HNET-USER1";
        String senderHash = "abcefg123hash";
        byte[] content = new byte[] { 1, 2, 3 };

        // When
        Message message = Message.builder()
                .recipientId(recipient)
                .senderIdHash(senderHash)
                .encryptedImage(content)
                .build();

        // Then
        assertEquals(recipient, message.getRecipientId());
        assertEquals(senderHash, message.getSenderIdHash());
        assertArrayEquals(content, message.getEncryptedImage());
    }

    @Test
    public void testOnCreate_ShouldSetTimestamp() {
        // Given
        Message message = new Message();
        assertNull(message.getCreatedAt(), "CreatedAt should be null initially");

        // When
        // Simulate what JPA does before persisting
        message.onCreate();

        // Then
        assertNotNull(message.getCreatedAt(), "CreatedAt timestamp should be generated");
    }

    @Test
    public void testNoArgsConstructor() {
        // When
        Message msg = new Message();

        // Then
        assertNotNull(msg);
        assertNull(msg.getRecipientId());
    }

    @Test
    public void testAllArgsConstructor() {
        // Given
        long id = 1L;
        String recipient = "HNET-USER1";
        String senderHash = "abcefg123hash";
        byte[] content = new byte[] { 1, 2, 3 };
        LocalDateTime now = LocalDateTime.now();

        // When
        Message msg = new Message(id, recipient, senderHash, content, now);

        // Then
        assertEquals(id, msg.getId());
        assertEquals(recipient, msg.getRecipientId());
        assertEquals(senderHash, msg.getSenderIdHash());
        assertArrayEquals(content, msg.getEncryptedImage());
        assertEquals(now, msg.getCreatedAt());
    }

    @Test
    public void testGettersAndSetters() {
        // Given
        Message msg = new Message();
        String recipient = "HNET-SETTER";

        // When
        msg.setRecipientId(recipient);

        // Then
        assertEquals(recipient, msg.getRecipientId());
    }
}

package com.hermnet.api.dto;

import java.time.LocalDateTime;

public record UserResponse(
    String id,
    String publicKey,
    LocalDateTime createdAt
) {}
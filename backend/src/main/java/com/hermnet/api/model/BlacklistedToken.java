package com.hermnet.api.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "token_blacklist")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BlacklistedToken {
    
     @Id
     @Column(length = 36, name = "jti")
     private String jti;

     @Column(name = "ravoked_reason", length = 20) 
     private String revokedReason;

     @Column(name = "expires_at", nullable = false)
     private LocalDateTime expiresAt;

     @PrePersist
     public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
     }
}

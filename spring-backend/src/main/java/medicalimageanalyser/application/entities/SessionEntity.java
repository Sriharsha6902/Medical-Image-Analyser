package medicalimageanalyser.application.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;


@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class SessionEntity {
    @Id
    private String sessionId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;
    private String email;
    private String refreshToken;

    @CreationTimestamp
    private Instant issuedAt;
    private Instant expiresAt;

    private boolean revoked;
}


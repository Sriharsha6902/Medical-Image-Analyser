package medicalimageanalyser.application.repository;


import medicalimageanalyser.application.entities.SessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface SessionRepository extends JpaRepository<SessionEntity, String> {
    List<SessionEntity> findByEmail(String email);

    @Modifying
    @Query("""
    DELETE FROM SessionEntity s
    WHERE (s.revoked = true OR s.expiresAt < :now)
      AND s.expiresAt < :oneWeekAgo
""")
    void deleteOldExpiredOrRevokedSessions(
            @Param("now") Instant now,
            @Param("oneWeekAgo") Instant oneWeekAgo
    );

}


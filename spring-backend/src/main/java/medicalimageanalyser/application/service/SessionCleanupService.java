package medicalimageanalyser.application.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import medicalimageanalyser.application.entities.SessionEntity;
import medicalimageanalyser.application.repository.SessionRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SessionCleanupService {

    private final SessionRepository sessionRepository;

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void cleanExpiredSessionsOlderThanAWeek() {
        Instant now = Instant.now();
        Instant oneWeekAgo = now.minus(7, ChronoUnit.DAYS);
        sessionRepository.deleteOldExpiredOrRevokedSessions(now, oneWeekAgo);
    }
}

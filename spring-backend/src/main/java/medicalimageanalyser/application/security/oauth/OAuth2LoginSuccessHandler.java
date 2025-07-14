package medicalimageanalyser.application.security.oauth;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import medicalimageanalyser.application.entities.SessionEntity;
import medicalimageanalyser.application.repository.SessionRepository;
import medicalimageanalyser.application.security.jwt.JwtUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final SessionRepository sessionRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        DefaultOAuth2User oauthUser = (DefaultOAuth2User) authentication.getPrincipal();

        String email = oauthUser.getAttribute("email");
        String name = oauthUser.getAttribute("name");
        String provider = oauthUser.getAttribute("provider");

        if (email == null || name == null || provider == null) {
            throw new ServletException("Invalid details");
        }

        String sessionId = UUID.randomUUID().toString();
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(7, ChronoUnit.DAYS);

        Map<String, Object> claims = Map.of(
                "name", name,
                "email", email,
                "authProvider", provider
        );

        String accessToken = jwtUtil.generateAccessToken(claims, email, sessionId);
        String refreshToken = jwtUtil.generateRefreshToken(email, sessionId);

        SessionEntity session = SessionEntity.builder()
                .sessionId(sessionId)
                .email(email)
                .refreshToken(refreshToken)
                .issuedAt(issuedAt)
                .expiresAt(expiresAt)
                .revoked(false)
                .build();
        sessionRepository.save(session);

        String redirectUrl = String.format("http://localhost:5173/oauth2/redirect?accessToken=%s&refreshToken=%s",
                accessToken, refreshToken);

        response.sendRedirect(redirectUrl);
    }
}

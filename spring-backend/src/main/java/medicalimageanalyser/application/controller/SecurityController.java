package medicalimageanalyser.application.controller;

import medicalimageanalyser.application.entities.SessionEntity;
import medicalimageanalyser.application.enums.AuthProvider;
import medicalimageanalyser.application.repository.SessionRepository;
import medicalimageanalyser.application.security.jwt.JwtUtil;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import medicalimageanalyser.application.repository.UserRepo;
import medicalimageanalyser.application.security.dtos.AuthResponse;
import medicalimageanalyser.application.security.dtos.LoginRequest;
import medicalimageanalyser.application.entities.UserEntity;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CsrfToken;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class SecurityController {

    private final JwtUtil jwtUtil;
    private final SessionRepository sessionRepository;
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    

    @PostMapping(path = "/register",consumes = "application/json")
    public ResponseEntity<AuthResponse> registerUser(@RequestBody UserEntity user) {
        AuthResponse response = new AuthResponse();
        if (userRepo.existsByEmail(user.getEmail())) {
            response.setResponse("Email already in use");
            return ResponseEntity.badRequest().body(response);
        }
        user.setProvider(AuthProvider.LOCAL);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if(user.getUsername()==null || user.getUsername().isEmpty()) user.setUsername(user.getEmail());

        userRepo.save(user);
        response.setResponse("User registered");
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            UserEntity user = userRepo.findByUsername(loginRequest.getUserCred())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUserCred(),
                            loginRequest.getPassword()
                    )
            );

            String sessionId = UUID.randomUUID().toString();

            Map<String, Object> claims = new HashMap<>(Map.of("provider", "LOCAL", "type", "access"));
            String accessToken = jwtUtil.generateAccessToken(claims,user.getUsername(), sessionId);
            String refreshToken = jwtUtil.generateRefreshToken(user.getUsername(), sessionId);

            SessionEntity session = new SessionEntity();
            session.setEmail(user.getEmail());
            session.setSessionId(sessionId);
            session.setUser(user);
            session.setRefreshToken(refreshToken);
            session.setExpiresAt(Instant.now().plus(7, ChronoUnit.DAYS));
            sessionRepository.save(session);

            Map<String, Object> response = Map.of(
                    "accessToken", accessToken,
                    "refreshToken", refreshToken,
                    "user", Map.of(
                            "username", user.getUsername(),
                            "email", user.getEmail(),
                            "name", user.getFirstName()
                    )
            );

            return ResponseEntity.ok(response);

        } catch (BadCredentialsException | UsernameNotFoundException ex) {
            String msg = "";
            if(ex.getMessage()!=null) msg = ex.getMessage();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    Map.of("error", msg)
            );
        }
    }


    @PostMapping(path = "/refresh", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> refreshAccessToken(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");

        if (refreshToken == null || !jwtUtil.isTokenValid(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid or expired refresh token"));
        }

        String email = jwtUtil.extractEmail(refreshToken);
        String sessionId = jwtUtil.extractSessionId(refreshToken);

        Optional<SessionEntity> sessionOpt = sessionRepository.findById(sessionId);
        if (sessionOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Session not found"));
        }

        SessionEntity session = sessionOpt.get();
        if (session.isRevoked() || session.getExpiresAt().isBefore(java.time.Instant.now())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Session expired or revoked"));
        }

        Map<String, Object> claims = Map.of(
                "email", email,
                "authProvider", "LOCAL"
        );
        String newAccessToken = jwtUtil.generateAccessToken(claims, email, sessionId);

        return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
    }

    @PostMapping("/logout-session")
    public ResponseEntity<?> logoutSession(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");
        if (!jwtUtil.isTokenValid(refreshToken)) {
            return ResponseEntity.badRequest().body("Invalid refresh token");
        }

        String sessionId = jwtUtil.extractSessionId(refreshToken);
        sessionRepository.findById(sessionId).ifPresent(session -> {
            session.setRevoked(true);
            sessionRepository.save(session);
        });

        return ResponseEntity.ok(Map.of("message", "Logged out from this session"));
    }

    @PostMapping("/session-valid")
    public ResponseEntity<Map<String, Boolean>> isSessionValid() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isValid = authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken);
        return ResponseEntity.ok(Map.of("valid", isValid));
    }


    @GetMapping("/csrf-token")
    public Map<String, String> csrfToken(CsrfToken token) {
        return Map.of("token", token.getToken());
    }

}

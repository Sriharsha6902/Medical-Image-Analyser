package medicalimageanalyser.application.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import medicalimageanalyser.application.repository.UserRepo;
import medicalimageanalyser.application.security.dtos.AuthResponse;
import medicalimageanalyser.application.security.dtos.LoginRequest;
import medicalimageanalyser.application.user.UserEntity;

import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class SecurityController {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    

    @PostMapping(path = "/register",consumes = "application/json")
    public ResponseEntity<AuthResponse> registerUser(@RequestBody UserEntity user) {
        AuthResponse response = new AuthResponse();
        if (userRepo.findByEmail(user.getEmail()).isPresent()) {
            response.setResponse("Email already in use");
            return ResponseEntity.badRequest().body(response);
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if(user.getUsername()==null || user.getUsername().isEmpty()) user.setUsername(user.getEmail());

        userRepo.save(user);
        response.setResponse("User registered");
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/login", consumes = "application/json",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
        AuthResponse response = new AuthResponse();
        Optional<UserEntity> user = this.userRepo.findByUsername(loginRequest.getUserCred());
        try {
            authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUserCred(),
                    loginRequest.getPassword()
                )
            );
            response.setResponse("Login successful");
            response.setUsername(user.map(UserEntity::getFirstName).orElse(null));
            return ResponseEntity.ok(response);   
        } catch (BadCredentialsException ex) {
            response.setResponse("Invalid credentials");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @GetMapping("/csrf-token")
    public Map<String, String> csrfToken(CsrfToken token) {
        return Map.of("token", token.getToken());
    }

}

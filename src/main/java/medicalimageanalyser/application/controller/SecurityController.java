package medicalimageanalyser.application.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import medicalimageanalyser.application.model.UserEntity;
import medicalimageanalyser.application.repository.UserRepo;
import medicalimageanalyser.application.security.dtos.LoginRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    public ResponseEntity<String> registerUser(@RequestBody UserEntity user) {
        if (userRepo.findByEmailAddress(user.getEmailAddress()).isPresent()) {
            return ResponseEntity.badRequest().body("Email already in use");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("ROLE_USER");
        user.setUsername(user.getEmailAddress());

        userRepo.save(user);

        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping(path = "/login", consumes = "application/json")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUserCred(),
                    loginRequest.getPassword()
                )
            );
            return ResponseEntity.ok("Login successful");   
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

}

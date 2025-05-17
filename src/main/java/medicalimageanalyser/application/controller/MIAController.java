package medicalimageanalyser.application.controller;

import java.util.Collections;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MIAController {
    @GetMapping("/me")
    public Map<String, Object> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return Collections.emptyMap();
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof OAuth2User) {
            return ((OAuth2User) principal).getAttributes();
        }

        return Collections.singletonMap("name", principal.toString());
    }
    @GetMapping("/csrf-token")
    public Map<String, String> csrfToken(CsrfToken token) {
        return Map.of("token", token.getToken());
    }

}

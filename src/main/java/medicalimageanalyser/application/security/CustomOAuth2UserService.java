package medicalimageanalyser.application.security;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import medicalimageanalyser.application.enums.AuthProvider;
import medicalimageanalyser.application.model.UserEntity;
import medicalimageanalyser.application.repository.UserRepo;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepo userRepo;

    public CustomOAuth2UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) {
        OAuth2User oauthUser = super.loadUser(request);
        Map<String, Object> attributes = oauthUser.getAttributes();

        String provider = request.getClientRegistration().getRegistrationId().toLowerCase();
        String email = getEmail(attributes, provider);
        String name = getName(attributes, provider);

        if (email != null) {
            Optional<UserEntity> existingUser = userRepo.findByEmailAddress(email);
            if (existingUser.isEmpty()) {
                UserEntity newUser = new UserEntity();
                newUser.setEmailAddress(email);
                newUser.setFirstName(name);
                newUser.setRole("ROLE_USER");
                newUser.setPassword(""); // no password
                newUser.setUsername(email);
                newUser.setAuthProvider(AuthProvider.valueOf(provider.toUpperCase()));
                userRepo.save(newUser);
                userRepo.save(newUser);
            }
        }

        String userNameAttribute = getUserNameAttribute(attributes, provider);

        return new DefaultOAuth2User(
            Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
            attributes,
            userNameAttribute
        );
    }

    private String getEmail(Map<String, Object> attrs, String provider) {
        if ("github".equals(provider)) {
            return (String) attrs.get("email");
        }
        if ("google".equals(provider)) {
            return (String) attrs.get("email");
        }
        if ("facebook".equals(provider)) {
            return (String) attrs.get("email");
        }
        return null;
    }

    private String getName(Map<String, Object> attrs, String provider) {
        if ("github".equals(provider)) {
            String name = (String) attrs.get("name");
            if (name == null || name.isBlank()) {
                name = (String) attrs.get("login");
            }
            return name;
        }
        if ("google".equals(provider)) {
            return (String) attrs.get("name");
        }
        if ("facebook".equals(provider)) {
            return (String) attrs.get("name");
        }
        return "OAuthUser";
    }

    private String getUserNameAttribute(Map<String, Object> attrs, String provider) {
        if ("github".equals(provider)) {
            if (attrs.containsKey("name")) return "name";
            if (attrs.containsKey("login")) return "login";
        }
        if ("google".equals(provider)) {
            if (attrs.containsKey("name")) return "name";
        }
        if ("facebook".equals(provider)) {
            if (attrs.containsKey("name")) return "name";
        }
        // fallback to any attribute key
        return attrs.keySet().stream().findFirst().orElse("name");
    }
}
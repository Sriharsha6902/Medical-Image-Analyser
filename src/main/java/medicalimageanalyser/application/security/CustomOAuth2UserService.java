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

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepo userRepo;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) {
        OAuth2User oauthUser = super.loadUser(request);
        Map<String, Object> attributes = oauthUser.getAttributes();

        String provider = request.getClientRegistration().getRegistrationId().toLowerCase();
        if(provider.equals("google")){
            oauthUsingGoogle(attributes);
        }
        else if(provider.equals("github")){
            oauthUsingGitHub(attributes);
        }
        else if(provider.equals("facebook"))
            oauthUsingFacebook(attributes);
        String userNameAttribute = getUserNameAttribute(attributes, provider);

        return new DefaultOAuth2User(
            Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
            attributes,
            userNameAttribute
        );
    }

    private void oauthUsingGoogle(Map<String, Object> attributes){
        String email = (String) attributes.get("email");
        if (email != null) {
            Optional<UserEntity> existingUser = userRepo.findByEmailAddress(email);
            if (existingUser.isEmpty()) {
                UserEntity newUser = new UserEntity();
                newUser.setEmailAddress(email);
                newUser.setFirstName((String) attributes.get("given_name"));
                newUser.setLastName((String) attributes.get("family_name"));
                newUser.setRole("ROLE_USER");
                newUser.setPassword("");
                newUser.setUsername(email);
                newUser.setAuthProvider(AuthProvider.GOOGLE);
                userRepo.save(newUser);
            }
        }
    }

    private void oauthUsingGitHub(Map<String, Object> attributes){
        String email = (String) attributes.get("email");
        String login = (String) attributes.get("login");
        if (email != null || login != null) {
            UserEntity existingUser = userRepo.findByEmailAddress(email).orElse(userRepo.findByUsername(login).get());
            if (existingUser == null) {
                UserEntity newUser = new UserEntity();
                newUser.setEmailAddress(email);
                newUser.setUsername(login);
                newUser.setFirstName((String) attributes.get("name"));
                newUser.setPassword("");
                newUser.setRole("ROLE_USER");
                newUser.setAuthProvider(AuthProvider.GITHUB);
                userRepo.save(newUser);
            }
        }
    }

    private void oauthUsingFacebook(Map<String,Object> attributes){
        String email = (String) attributes.get("email");
        if(email != null){
            Optional<UserEntity> exiUser = userRepo.findByEmailAddress(email);
            if(exiUser.isEmpty()){
                UserEntity newUser = new UserEntity();
                newUser.setAuthProvider(AuthProvider.FACEBOOK);
                newUser.setEmailAddress(email);
                newUser.setFirstName((String) attributes.get("name"));
                newUser.setPassword("");
                newUser.setRole("ROLE_USER");
                newUser.setUsername(email);
            }
        }
    }

    private String getUserNameAttribute(Map<String, Object> attrs, String provider) {
        if ("github".equals(provider)) {
            if (attrs.containsKey("name") && attrs.get("name") != null) return "name";
            if (attrs.containsKey("login") && attrs.get("login") != null) return "login";
        }
        if ("google".equals(provider)) {
            if (attrs.containsKey("name")) return "name";
        }
        if ("facebook".equals(provider)) {
            if (attrs.containsKey("name")) return "name";
        }
        return attrs.keySet().stream().findFirst().orElse("name");
    }
}
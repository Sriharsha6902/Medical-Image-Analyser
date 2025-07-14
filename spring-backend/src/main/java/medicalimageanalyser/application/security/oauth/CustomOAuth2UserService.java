package medicalimageanalyser.application.security.oauth;

import java.util.Collections;
import java.util.Map;

import medicalimageanalyser.application.enums.AuthProvider;
import medicalimageanalyser.application.repository.UserRepo;
import medicalimageanalyser.application.entities.UserEntity;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final String emailAtt = "email";
    private final String loginAtt = "login";

    private final UserRepo userRepo;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) {
        OAuth2User oauthUser = super.loadUser(request);
        Map<String, Object> attributes = oauthUser.getAttributes();
        String provider = request.getClientRegistration().getRegistrationId().toLowerCase();

        String email = (String) attributes.get(emailAtt);
        if(!userRepo.existsByEmail(email)){
            switch (provider) {
                case "google" -> oauthUsingGoogle(attributes);
                case "github" -> oauthUsingGitHub(attributes);
                case "facebook" -> oauthUsingFacebook(attributes);
            }
        }

        String userNameAttribute = getUserNameAttribute(attributes, provider);
        return new DefaultOAuth2User(
            Collections.emptyList(),
            attributes,
            userNameAttribute
        );

    }

    private void oauthUsingGoogle(Map<String, Object> attributes){
        String email = (String) attributes.get(emailAtt);
        UserEntity newUser = new UserEntity();
        newUser.setEmail(email);
        newUser.setFirstName((String) attributes.get("given_name"));
        newUser.setLastName((String) attributes.get("family_name"));
        newUser.setPassword("");
        newUser.setUsername(email);
        newUser.setProvider(AuthProvider.GOOGLE);
        userRepo.save(newUser);
    }

    private void oauthUsingGitHub(Map<String, Object> attributes){
        String email = (String) attributes.get(emailAtt);
        String login = (String) attributes.get(loginAtt);
        UserEntity newUser = new UserEntity();
        newUser.setEmail(email);
        if(login!=null && !userRepo.existsByUsername(login)){
            newUser.setUsername(login);
        }
        else newUser.setUsername(email);
        newUser.setUsername(email);
        newUser.setFirstName((String) attributes.get("name"));
        newUser.setPassword("");
        newUser.setProvider(AuthProvider.GITHUB);
        userRepo.save(newUser);
    }

    private void oauthUsingFacebook(Map<String,Object> attributes){
        String email = (String) attributes.get(emailAtt);
        UserEntity newUser = new UserEntity();
        newUser.setProvider(AuthProvider.FACEBOOK);
        newUser.setEmail(email);
        newUser.setFirstName((String) attributes.get("name"));
        newUser.setPassword("");
        newUser.setUsername(email);
        userRepo.save(newUser);
    }

    private String getUserNameAttribute(Map<String, Object> attrs, String provider) {
        if ("github".equals(provider)) {
            if (attrs.containsKey("name") && attrs.get("name") != null) return "name";
            if (attrs.containsKey(loginAtt) && attrs.get(loginAtt) != null) return loginAtt;
        }
        if ("google".equals(provider) && attrs.containsKey("name")) return "name";
        
        if ("facebook".equals(provider) && attrs.containsKey("name")) return "name";
        
        return attrs.keySet().stream().findFirst().orElse("name");
    }
}
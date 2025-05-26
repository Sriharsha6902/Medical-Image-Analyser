package medicalimageanalyser.application.service;

import java.util.Collections;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import medicalimageanalyser.application.enums.AuthProvider;
import medicalimageanalyser.application.model.UserEntity;
import medicalimageanalyser.application.repository.UserRepo;

@Service
public class SecurityService implements UserDetailsService{

    private final UserRepo userRepo;

    public SecurityService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }


    @Override
    public UserDetails loadUserByUsername(String userCred) throws UsernameNotFoundException {
        UserEntity user = userRepo.findByEmailAddress(userCred)
            .orElse(userRepo.findByUsername(userCred)
            .orElseThrow(() -> new UsernameNotFoundException("User not found")));

        if (user.getAuthProvider() != AuthProvider.LOCAL) {
            throw new UsernameNotFoundException("This user is registered via " + user.getAuthProvider() + ". Please login via " + user.getAuthProvider());
        }

        return new User(
            user.getUsername(),
            user.getPassword(),
            Collections.singleton(() -> user.getRole())
        );
    }

}

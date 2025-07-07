package medicalimageanalyser.application.service;

import java.util.Collections;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import medicalimageanalyser.application.enums.AuthProvider;
import medicalimageanalyser.application.repository.UserRepo;
import medicalimageanalyser.application.user.UserEntity;

@Service
@RequiredArgsConstructor
public class SecurityService implements UserDetailsService{

    private final UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String userCred) throws UsernameNotFoundException {
        UserEntity user = userRepo.findByEmail(userCred)
            .orElse(userRepo.findByUsername(userCred)
            .orElseThrow(() -> new UsernameNotFoundException("User not found")));

        if (user.getProvider() != AuthProvider.LOCAL) {
            throw new UsernameNotFoundException("This user is registered via " + user.getProvider() + ". Please login via " + user.getProvider());
        }

        return new User(
            user.getUsername(),
            user.getPassword(),
            Collections.emptyList()
        );
    }

}

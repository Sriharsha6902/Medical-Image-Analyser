package medicalimageanalyser.application.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import medicalimageanalyser.application.entities.UserEntity;


public interface UserRepo extends JpaRepository<UserEntity,Integer>{
    Optional<UserEntity> findByUsername(String username);
    Optional<UserEntity> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
}

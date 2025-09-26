package com.example.server.Repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.server.Entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);
}

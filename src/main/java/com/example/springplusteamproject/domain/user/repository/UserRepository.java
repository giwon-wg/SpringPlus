package com.example.springplusteamproject.domain.user.repository;

import com.example.springplusteamproject.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Boolean existsByEmail(String email);
    Boolean existsByNickname(String nickname);
}

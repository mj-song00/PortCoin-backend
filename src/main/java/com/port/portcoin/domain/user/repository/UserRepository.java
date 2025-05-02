package com.port.portcoin.domain.user.repository;

import java.util.Optional;
import java.util.UUID;
import com.port.portcoin.domain.user.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(@NotBlank @Email String email);
    Optional<User> findByNickName(@NotBlank String nickname);
}

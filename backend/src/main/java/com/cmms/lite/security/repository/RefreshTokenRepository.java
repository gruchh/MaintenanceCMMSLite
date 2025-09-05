package com.cmms.lite.security.repository;

import com.cmms.lite.security.entity.RefreshToken;
import com.cmms.lite.security.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);
    int deleteByUser(User user);
    void deleteByToken(String token);
}
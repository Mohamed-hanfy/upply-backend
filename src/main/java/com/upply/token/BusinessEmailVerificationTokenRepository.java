package com.upply.token;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BusinessEmailVerificationTokenRepository extends JpaRepository<BusinessEmailVerificationToken, Integer> {

    Optional<BusinessEmailVerificationToken> findByToken(String token);

    @Query("""
        SELECT t FROM BusinessEmailVerificationToken t
        WHERE t.token = :tokenString AND t.used = false
        ORDER BY t.createdAt DESC
        LIMIT 1
    """)
    Optional<BusinessEmailVerificationToken> findUnusedByTokenString(String tokenString);

    @Query("""
        SELECT t FROM BusinessEmailVerificationToken t
        WHERE t.user.id = :userId AND t.used = false
        ORDER BY t.createdAt DESC
        LIMIT 1
    """)
    Optional<BusinessEmailVerificationToken> findMostRecentUnusedTokenByUserId(Long userId);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("""
        UPDATE BusinessEmailVerificationToken T
        SET T.used = true
        WHERE T.user.id = :userId AND T.used = false
    """)
    void markAllTokensAsUsedForUser(Long userId);
}
package com.eventhub.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

interface UserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByEmailIgnoreCase(String email);
    Optional<AppUser> findBySessionToken(String sessionToken);
    List<AppUser> findByEmailVerifiedTrueAndNameIsNotNull();
}

package com.vagsoft.bookstore.repositories;

import com.vagsoft.bookstore.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.config.annotation.web.PortMapperDsl;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for authentication-related database operations
 */
@Repository
public interface AuthRepository extends JpaRepository<User, Integer> {
    User findByUsername(String username);
}

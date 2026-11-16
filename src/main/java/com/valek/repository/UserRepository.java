package com.valek.repository;

import com.valek.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByUsernameIgnoreCase(String username);

    @Query("""
            SELECT u FROM User u
            WHERE LOWER(u.email) = LOWER(:login)
               OR LOWER(u.username) = LOWER(:login)
            """)
    Optional<User> findByLogin(@Param("login") String login);
}
package com.recode.backend.user.repository;

import com.recode.backend.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);
    Optional<User> findByLogin(String login);
    List<User> findByEmailContainingIgnoreCase(String email);
    List<User> findAllByName(String name);

}
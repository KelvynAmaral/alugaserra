package com.alugaserra.repository;

import com.alugaserra.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    // O Spring Data JPA é inteligente o suficiente para usar este método para o UserDetailsService também.
    Optional<User> findByEmail(String email);

}


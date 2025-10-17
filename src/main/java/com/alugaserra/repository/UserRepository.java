package com.alugaserra.repository;

import com.alugaserra.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    // CORREÇÃO: O método deve retornar Optional<User> para ser usado em toda a aplicação.
    // O Spring Data JPA é inteligente o suficiente para usar este método para o UserDetailsService também.
    Optional<User> findByEmail(String email);

}



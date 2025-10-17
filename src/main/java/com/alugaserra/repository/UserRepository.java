package com.alugaserra.repository;

import com.alugaserra.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    /**
     * Busca um usuário pelo seu endereço de e-mail.
     * Usado pelo Spring Security para autenticar.
     * @param email O e-mail a ser buscado.
     * @return UserDetails contendo as informações do usuário encontrado.
     */
    UserDetails findByEmail(String email);
}

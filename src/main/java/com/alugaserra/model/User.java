package com.alugaserra.model;

import com.alugaserra.enums.UserRole;
import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    private String passwordHash;

    @Column(unique = true)
    private String cpf;

    private String phone;

    @Enumerated(EnumType.STRING)
    private UserRole role; // LOCADOR, INQUILINO, ADMIN

    private boolean verified = false; // Para verificação de e-mail
}

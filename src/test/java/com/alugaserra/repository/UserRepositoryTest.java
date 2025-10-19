package com.alugaserra.repository;

import com.alugaserra.enums.UserRole;
import com.alugaserra.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

// @DataJpaTest configura um ambiente focado apenas na camada de persistência.
@DataJpaTest
// Esta anotação diz ao Spring para NÃO substituir nosso banco de dados configurado.
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    // TestEntityManager é um ajudante para manipular as nossas entidades nos testes.
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Deve encontrar um usuário com sucesso pelo seu email")
    void findByEmail_ShouldReturnUser_WhenEmailExists() {

        // Criamos um usuário de teste
        User newUser = new User();
        newUser.setEmail("teste.repository@email.com");
        newUser.setPasswordHash("hash_qualquer");
        newUser.setRole(UserRole.INQUILINO);

        // Usamos o entityManager para salvar o usuário na base de dados de teste
        entityManager.persistAndFlush(newUser);


        // Executamos o método que queremos testar
        Optional<User> foundUserOptional = userRepository.findByEmail("teste.repository@email.com");

        // --- Verificação (Assert) ---
        // Verificamos se o Optional não está vazio
        assertThat(foundUserOptional).isPresent();
        // Verificamos se o email do usuário encontrado é o que esperamos
        assertThat(foundUserOptional.get().getEmail()).isEqualTo(newUser.getEmail());
    }

    @Test
    @DisplayName("Deve retornar um Optional vazio ao procurar por um email que não existe")
    void findByEmail_ShouldReturnEmpty_WhenEmailDoesNotExist() {

        Optional<User> foundUserOptional = userRepository.findByEmail("email.inexistente@email.com");

        // --- Verificação (Assert) ---
        // Verificamos se o Optional está vazio
        assertThat(foundUserOptional).isNotPresent();
    }
}




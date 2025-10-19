package com.alugaserra.repository;

import com.alugaserra.enums.PropertyStatus;
import com.alugaserra.enums.PropertyType;
import com.alugaserra.enums.UserRole;
import com.alugaserra.model.Property;
import com.alugaserra.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PropertyRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PropertyRepository propertyRepository;

    private User owner;

    @BeforeEach
    void setUp() {
        // --- Cenário (Arrange) ---
        // Primeiro, criamos e salvamos um proprietário (User)
        owner = new User();
        owner.setEmail("owner.test@email.com");
        owner.setPasswordHash("hash");
        owner.setRole(UserRole.LOCADOR);
        entityManager.persist(owner);

        // Criamos uma propriedade ativa associada a este proprietário
        Property activeProperty = new Property();
        activeProperty.setOwner(owner);
        activeProperty.setTitle("Casa Ativa");
        activeProperty.setType(PropertyType.CASA);
        activeProperty.setStatus(PropertyStatus.ACTIVE);
        entityManager.persist(activeProperty);

        // Criamos uma propriedade alugada associada ao mesmo proprietário
        Property rentedProperty = new Property();
        rentedProperty.setOwner(owner);
        rentedProperty.setTitle("Casa Alugada");
        rentedProperty.setType(PropertyType.APARTAMENTO);
        rentedProperty.setStatus(PropertyStatus.RENTED);
        entityManager.persist(rentedProperty);

        entityManager.flush(); // Garante que os dados sejam salvos no banco de dados de teste
    }

    @Test
    @DisplayName("Deve encontrar apenas as propriedades com o status ACTIVE")
    void findByStatus_ShouldReturnOnlyActiveProperties() {
        // --- Ação (Act) ---
        List<Property> activeProperties = propertyRepository.findByStatus(PropertyStatus.ACTIVE);

        // --- Verificação (Assert) ---
        assertThat(activeProperties).hasSize(1); // Esperamos encontrar apenas 1 propriedade
        assertThat(activeProperties.get(0).getTitle()).isEqualTo("Casa Ativa");
    }

    @Test
    @DisplayName("Deve contar corretamente o número de propriedades ativas de um proprietário")
    void countByOwnerAndStatus_ShouldReturnCorrectCount() {
        // --- Ação (Act) ---
        long count = propertyRepository.countByOwnerAndStatus(owner, PropertyStatus.ACTIVE);

        // --- Verificação (Assert) ---
        assertThat(count).isEqualTo(1);
    }
}



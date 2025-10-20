package com.alugaserra.service;

import com.alugaserra.dto.PropertyResponseDto;
import com.alugaserra.dto.UserAdminViewDto;
import com.alugaserra.enums.UserRole;
import com.alugaserra.model.User;
import com.alugaserra.repository.PropertyRepository;
import com.alugaserra.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Contém a lógica de negócio para operações realizadas por administradores.
 */
@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    // 1. INJETAR O REPOSITÓRIO DE IMÓVEIS
    @Autowired
    private PropertyRepository propertyRepository;

    // 2. INJETAR O PROPERTYSERVICE PARA REUTILIZAR A LÓGICA DE CONVERSÃO DE DTO
    @Autowired
    private PropertyService propertyService;


    /**
     * Atualiza o papel (role) de um usuário específico.
     * @param userId O ID do usuário a ser atualizado.
     * @param newRole O novo papel a ser atribuído.
     * @return O DTO do usuário atualizado.
     */
    @Transactional
    public UserAdminViewDto updateUserRole(UUID userId, UserRole newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com o ID: " + userId));

        user.setRole(newRole);

        User updatedUser = userRepository.save(user);

        return new UserAdminViewDto(updatedUser);
    }

    /**
     * Retorna uma lista de TODOS os imóveis do sistema, independentemente do status.
     * @return Uma lista de DTOs de imóveis.
     */
    public List<PropertyResponseDto> findAllProperties() {
        return propertyRepository.findAll()
                .stream()
                .map(propertyService::convertToDto) // Reutiliza o método de conversão
                .collect(Collectors.toList());
    }

    /**
     * Deleta um imóvel do sistema. Ação de moderador.
     * @param propertyId O ID do imóvel a ser deletado.
     */
    public void deleteProperty(UUID propertyId) {
        if (!propertyRepository.existsById(propertyId)) {
            throw new RuntimeException("Imóvel não encontrado com o ID: " + propertyId);
        }
        propertyRepository.deleteById(propertyId);
    }
}


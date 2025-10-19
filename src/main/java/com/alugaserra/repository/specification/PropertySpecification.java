package com.alugaserra.repository.specification;

import com.alugaserra.enums.PropertyStatus;
import com.alugaserra.enums.PropertyType;
import com.alugaserra.model.Property;
import org.springframework.data.jpa.domain.Specification;

/**
 * Classe utilitária para criar especificações (filtros) dinâmicas para a entidade Property.
 */
public class PropertySpecification {

    /**
     * Retorna uma especificação que filtra propriedades pelo tipo.
     */
    public static Specification<Property> hasType(PropertyType type) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("type"), type);
    }

    /**
     * Retorna uma especificação que filtra propriedades com valor de aluguel menor ou igual ao valor máximo.
     */
    public static Specification<Property> maxRent(Double maxRent) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(root.get("rentValue"), maxRent);
    }

    /**
     * Retorna uma especificação que filtra propriedades com um número de quartos maior ou igual ao mínimo.
     */
    public static Specification<Property> minRooms(Integer minRooms) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("rooms"), minRooms);
    }

    /**
     * Retorna uma especificação que garante que a propriedade tenha garagem.
     */
    public static Specification<Property> hasGarage() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.isTrue(root.get("hasGarage"));
    }

    /**
     * Retorna uma especificação que filtra apenas as propriedades com status ACTIVE.
     * nosso filtro base.
     */
    public static Specification<Property> isActive() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("status"), PropertyStatus.ACTIVE);
    }
}

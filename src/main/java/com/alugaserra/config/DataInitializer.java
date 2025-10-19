package com.alugaserra.config;


import com.alugaserra.enums.PropertyType;
import com.alugaserra.enums.UserRole;
import com.alugaserra.model.Plan;
import com.alugaserra.model.Property;
import com.alugaserra.model.Subscription;
import com.alugaserra.model.User;
import com.alugaserra.repository.PlanRepository;
import com.alugaserra.repository.PropertyRepository;
import com.alugaserra.repository.SubscriptionRepository;
import com.alugaserra.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Configuration
public class DataInitializer implements CommandLineRunner {

    @Autowired private PlanRepository planRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private PropertyRepository propertyRepository;
    @Autowired private SubscriptionRepository subscriptionRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Override
    @Transactional // Garante que todas as operações sejam executadas numa única transação
    public void run(String... args) {
        createDefaultPlans();
        createDefaultProperties();
    }

    private void createDefaultPlans() {
        if (planRepository.count() == 0) {
            Plan bronze = new Plan();
            bronze.setName("Bronze");
            bronze.setMaxProperties(1);
            bronze.setPrice(19.90);
            bronze.setFeatures("1 imóvel ativo; Até 10 fotos + 1 vídeo");

            Plan prata = new Plan();
            prata.setName("Prata");
            prata.setMaxProperties(3);
            prata.setPrice(39.90);
            prata.setFeatures("Até 3 imóveis ativos; Destaque nas buscas");

            Plan ouro = new Plan();
            ouro.setName("Ouro");
            ouro.setMaxProperties(5);
            ouro.setPrice(59.90);
            ouro.setFeatures("Até 5 imóveis; Destaque + Selo 'Top Locador'");

            planRepository.saveAll(List.of(bronze, prata, ouro));
            System.out.println(">>> Planos padrão cadastrados no banco de dados.");
        }
    }

    private void createDefaultProperties() {
        if (propertyRepository.count() == 0 && userRepository.count() == 0) {
            // 1. Criar um usuário LOCADOR
            User locador = new User();
            locador.setName("Locador Exemplo");
            locador.setEmail("locador@email.com");
            locador.setPasswordHash(passwordEncoder.encode("senha123"));
            locador.setRole(UserRole.LOCADOR);
            locador.setCpf("11122233344");
            locador.setPhone("31911112222");
            userRepository.save(locador);

            // 2. Dar a ele uma assinatura "Ouro" para poder cadastrar vários imóveis
            Plan planOuro = planRepository.findByName("Ouro").orElseThrow();
            Subscription subscription = new Subscription();
            subscription.setUser(locador);
            subscription.setPlan(planOuro);
            subscription.setStatus("ACTIVE");
            subscription.setStartDate(LocalDate.now());
            subscriptionRepository.save(subscription);

            // 3. Criar uma lista de imóveis diversos
            Property casaGrande = new Property();
            casaGrande.setOwner(locador);
            casaGrande.setTitle("Casa Grande com Quintal");
            casaGrande.setType(PropertyType.CASA);
            casaGrande.setRentValue(2500.00);
            casaGrande.setRooms(4);
            casaGrande.setBathrooms(3);
            casaGrande.setHasGarage(true);

            Property aptoCentro = new Property();
            aptoCentro.setOwner(locador);
            aptoCentro.setTitle("Apartamento no Centro");
            aptoCentro.setType(PropertyType.APARTAMENTO);
            aptoCentro.setRentValue(1200.00);
            aptoCentro.setRooms(2);
            aptoCentro.setBathrooms(1);
            aptoCentro.setHasGarage(false);

            Property kitnetEstudante = new Property();
            kitnetEstudante.setOwner(locador);
            kitnetEstudante.setTitle("Kitnet ideal para estudantes");
            kitnetEstudante.setType(PropertyType.KITNET);
            kitnetEstudante.setRentValue(800.00);
            kitnetEstudante.setRooms(1);
            kitnetEstudante.setBathrooms(1);
            kitnetEstudante.setHasGarage(false);

            propertyRepository.saveAll(List.of(casaGrande, aptoCentro, kitnetEstudante));
            System.out.println(">>> Usuário e imóveis de exemplo cadastrados no banco de dados.");
        }
    }
}

package com.alugaserra.config;

import com.alugaserra.enums.PropertyStatus;
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
    @Transactional
    public void run(String... args) throws Exception {
        createDefaultPlans();
        createDefaultUsersAndProperties(); // Renomeado para mais clareza
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

    private void createDefaultUsersAndProperties() {
        if (userRepository.count() == 0) {
            // 1. Criar um usuário ADMIN
            User admin = new User();
            admin.setName("Admin Geral");
            admin.setEmail("admin@email.com");
            admin.setPasswordHash(passwordEncoder.encode("admin123"));
            admin.setRole(UserRole.ADMIN);
            admin.setCpf("00000000000");
            admin.setPhone("00000000000");
            userRepository.save(admin);

            // 2. Criar um usuário LOCADOR
            User locador = new User();
            locador.setName("Locador Exemplo");
            locador.setEmail("locador@email.com");
            locador.setPasswordHash(passwordEncoder.encode("senha123"));
            locador.setRole(UserRole.LOCADOR);
            locador.setCpf("11122233344");
            locador.setPhone("31911112222");
            userRepository.save(locador);

            // 3. Dar ao locador uma assinatura "Ouro"
            Plan planOuro = planRepository.findByName("Ouro").orElseThrow();
            Subscription subscription = new Subscription();
            subscription.setUser(locador);
            subscription.setPlan(planOuro);
            subscription.setStatus("ACTIVE");
            subscription.setStartDate(LocalDate.now());
            subscriptionRepository.save(subscription);

            // 4. Criar imóveis de exemplo
            Property casaGrande = new Property();
            casaGrande.setOwner(locador);
            casaGrande.setTitle("Casa Grande com Quintal");
            casaGrande.setType(PropertyType.CASA);
            // ... (restante das propriedades)
            propertyRepository.save(casaGrande);

            System.out.println(">>> Usuários e imóveis de exemplo cadastrados.");
        }
    }
}


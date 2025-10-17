package com.alugaserra.config;

import com.alugaserra.model.Plan;
import com.alugaserra.repository.PlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private PlanRepository planRepository;

    @Override
    public void run(String... args) throws Exception {
        // Verifica se os planos já existem para não duplicar
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
}

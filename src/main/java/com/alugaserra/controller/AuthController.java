package com.alugaserra.controller;

import com.alugaserra.dto.LoginRequestDto;
import com.alugaserra.dto.LoginResponseDto;
import com.alugaserra.dto.RegisterRequestDto;
import com.alugaserra.model.Plan;
import com.alugaserra.model.Subscription;
import com.alugaserra.model.User;
import com.alugaserra.repository.PlanRepository;
import com.alugaserra.repository.SubscriptionRepository;
import com.alugaserra.repository.UserRepository;
import com.alugaserra.service.TokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private PlanRepository planRepository;
    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody @Valid LoginRequestDto data) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.password());
        var auth = this.authenticationManager.authenticate(usernamePassword);
        var token = tokenService.generateToken((User) auth.getPrincipal());
        return ResponseEntity.ok(new LoginResponseDto(token));
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid RegisterRequestDto data) {
        if (this.userRepository.findByEmail(data.email()).isPresent()) {
            return ResponseEntity.badRequest().build(); // Retorna 400 se o email já existir
        }

        String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());

        // Usuário com o construtor vazio e usamos os setters
        User newUser = new User();
        newUser.setName(data.name());
        newUser.setEmail(data.email());
        newUser.setPasswordHash(encryptedPassword);
        newUser.setCpf(data.cpf());
        newUser.setPhone(data.phone());
        newUser.setRole(data.role());

        this.userRepository.save(newUser);

        // Atribui o plano Bronze como padrão para o novo usuário.
        Plan defaultPlan = planRepository.findByName("Bronze")
                .orElseThrow(() -> new RuntimeException("Erro crítico: Plano Bronze não encontrado no banco de dados!"));

        Subscription newSubscription = new Subscription();
        newSubscription.setUser(newUser);
        newSubscription.setPlan(defaultPlan);
        newSubscription.setStartDate(LocalDate.now());
        newSubscription.setEndDate(LocalDate.now().plusMonths(1)); // Define uma validade inicial de 1 mês
        newSubscription.setStatus("ACTIVE");

        subscriptionRepository.save(newSubscription);

        return ResponseEntity.status(201).build();
    }
}
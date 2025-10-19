# 🏷️ AlugaSerra - Backend

![Java](https://img.shields.io/badge/Java-17-orange?logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.0-green?logo=springboot&logoColor=white)
![License](https://img.shields.io/badge/license-MIT-blue)
![Build](https://img.shields.io/badge/build-passing-brightgreen)

> API RESTful para a plataforma de aluguel de imóveis **AlugaSerra**, construída com **Java** e **Spring Boot**.

---

## 💡 Sobre o Projeto

O **AlugaSerra** é uma plataforma SaaS que conecta locadores e inquilinos do bairro Serra (BH).  
Seu foco é oferecer uma solução digital simples e segura para o mercado de aluguéis local, com **assinaturas para locadores** e **busca gratuita para inquilinos**.

---

## ⚙️ Funcionalidades

✅ Autenticação segura com **JWT**  
🏠 CRUD de imóveis completo  
💳 Controle de planos (Bronze, Prata, Ouro)  
☁️ Upload de mídia via **AWS S3**  
📖 Documentação dinâmica via **Swagger**  
🚀 Próximos passos: filtros e chat em tempo real  

---

## 🧩 Estrutura do Projeto

```
📦 alugaserra-backend
 ┣ 📂 src
 ┃ ┣ 📂 main
 ┃ ┃ ┣ 📂 java/com/alugaserra
 ┃ ┃ ┃ ┣ 📂 config       # Configurações (Security, CORS, Swagger, S3)
 ┃ ┃ ┃ ┣ 📂 controller   # Endpoints da API
 ┃ ┃ ┃ ┣ 📂 dto          # Data Transfer Objects
 ┃ ┃ ┃ ┣ 📂 model        # Entidades JPA
 ┃ ┃ ┃ ┣ 📂 repository   # Interfaces JPA
 ┃ ┃ ┃ ┣ 📂 security     # Filtros e JWT
 ┃ ┃ ┃ ┗ 📂 service      # Lógica de negócio
 ┃ ┗ 📂 resources
 ┃   ┗ 📜 application.properties
 ┗ 📜 pom.xml
```


---

## 🧰 Tecnologias Utilizadas

| Categoria | Tecnologias |
|------------|--------------|
| **Linguagem & Framework** | ☕ Java 17+, 🚀 Spring Boot |
| **Segurança** | 🔐 Spring Security + JWT |
| **Banco de Dados** | 🐘 PostgreSQL 14+ |
| **Infraestrutura** | ☁️ AWS S3 |
| **Testes** | 🧪 JUnit |
| **Build & Gestão** | 🧰 Maven |
| **Documentação** | 🧩 Swagger |

---

## 🖥️ Instalação e Execução

### Navegue até a pasta do projeto

```bash
cd alugaserra-backend
```

### Configure as variáveis de ambiente

```bash
# Na pasta src/main/resources, crie o arquivo application.properties
# (baseado no exemplo application.properties.example)
```

> ⚠️ O arquivo `application.properties` está no `.gitignore` para proteger suas credenciais.

### Execute a aplicação

```bash
mvn spring-boot:run
```

A API estará disponível em:  
👉 [http://localhost:8080](http://localhost:8080)

---

## 🧪 Testes

Execute todos os testes com:

```bash
mvn test
```

---

## 📖 Documentação da API

A documentação completa dos endpoints é gerada com **Swagger (OpenAPI)**.

Acesse:  
👉 [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

> Inclui autenticação via token JWT (use o botão *Authorize*).

---

## 📅 Roadmap

- [ ] Implementar filtros de busca avançados  
- [ ] Chat em tempo real com Spring WebSocket  
- [ ] Integração com Stripe/MercadoPago  
- [ ] Painel administrativo  
- [ ] Autenticação via Google OAuth  



## 🤝 Contribuição

💬 Contribuições são **super bem-vindas**!  
Abra uma *issue* ou envie um *pull request* com melhorias ou novas ideias.

---

## 🧑‍💻 Autor

**Kelvyn Amaral**  
<sub>Desenvolvedor Java</sub>  

💼 [LinkedIn](https://www.linkedin.com/in/kelvyncandido/) 

---

## ⚠️ Status do Projeto

🚧 **Projeto em construção.**  
Novas funcionalidades e melhorias estão sendo desenvolvidas ativamente.

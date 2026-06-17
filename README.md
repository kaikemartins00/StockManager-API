# 📦 StockManager API

<p align="center">
  <img src="https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.x-green?style=for-the-badge&logo=springboot">
  <img src="https://img.shields.io/badge/PostgreSQL-Database-blue?style=for-the-badge&logo=postgresql">
  <img src="https://img.shields.io/badge/Docker-Container-blue?style=for-the-badge&logo=docker">
  <img src="https://img.shields.io/badge/JWT-Security-black?style=for-the-badge&logo=jsonwebtokens">
</p>


# 🚀 Sobre o projeto

O **StockManager API** é uma aplicação backend desenvolvida para gerenciamento de estoque, permitindo controle de produtos, categorias e movimentações de entrada e saída.

O projeto foi desenvolvido utilizando **Java + Spring Boot**, aplicando boas práticas de desenvolvimento, arquitetura em camadas, autenticação com JWT, controle de permissões, testes automatizados e documentação completa da API.


# 🌐 Deploy

A aplicação está disponível em ambiente de produção:

🔗 **API:**

https://stockmanager-api-febq.onrender.com


📚 **Swagger Documentation:**

https://stockmanager-api-febq.onrender.com/swagger-ui/index.html#/


## 🎥 Demonstração

<p align="center">
  <img src="./assets/demo.gif">
</p>


# ✨ Funcionalidades


## 🔐 Autenticação e autorização

- Cadastro de usuários
- Login utilizando JWT
- Geração de Bearer Token
- Autenticação utilizando Spring Security
- Controle de acesso baseado em roles
- Diferentes permissões para usuários


## 📦 Gerenciamento de produtos

- Cadastro de produtos
- Atualização de produtos
- Busca por SKU
- Listagem de produtos
- Exclusão de produtos
- Controle de quantidade em estoque


## 🔄 Controle de movimentações

- Entrada de produtos no estoque
- Saída de produtos
- Validação de quantidade disponível
- Histórico completo das movimentações
- Registro do usuário responsável pela operação
- Data e horário da movimentação


## 🏷️ Categorias

- Cadastro de categorias
- Padronização utilizando ENUM
- Associação entre produtos e categorias


# 🛠️ Tecnologias utilizadas


### Backend

- Java 21
- Spring Boot
- Spring Security
- JWT
- Spring Data JPA
- Hibernate
- Maven


### Banco de dados

- PostgreSQL


### Testes

- JUnit 5
- Mockito


### Documentação

- Swagger / OpenAPI


### Infraestrutura e Deploy

- Docker
- Docker Compose
- Render (Deploy da API)
- Variáveis de ambiente


### Ferramentas

- Postman
- Git
- GitHub


# 🏗️ Arquitetura do projeto

O projeto utiliza arquitetura em camadas (**Layered Architecture**), separando responsabilidades entre componentes.


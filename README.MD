# WEGpark API

API REST desenvolvida para apoiar o gerenciamento básico de um estacionamento corporativo da WEG. A aplicação centraliza o cadastro de usuários e veículos, o controle de associações, o registro de ocorrências e o envio de notificações.

> **Aviso:** este projeto foi criado em um contexto **100% educacional**, durante um processo de aprendizagem de desenvolvimento backend. Ele não representa um sistema oficial da WEG e não possui finalidade comercial.

## Sobre o projeto

O WEGpark permite que colaboradores e visitantes mantenham seus dados e veículos, enquanto guardas e profissionais de RH executam operações relacionadas à segurança e à administração do estacionamento.

Entre os principais fluxos da API estão:

- cadastro de colaboradores e visitantes;
- autenticação e autorização com JWT;
- validação de conta por e-mail;
- recuperação e alteração de senha;
- consulta e atualização de perfis;
- cadastro e associação de veículos;
- registro e consulta de ocorrências;
- gerenciamento de usuários por profissionais de RH;
- notificações internas e envio de e-mails.

## Arquitetura

A aplicação utiliza uma arquitetura de **monólito modular**. O código é separado por contexto de negócio e cada módulo mantém internamente seus controllers, DTOs, serviços, mappers, entidades e repositórios.

Os módulos se comunicam principalmente por eventos do Spring, reduzindo o acoplamento direto entre eles.

### Módulos da aplicação

| Módulo | Responsabilidade |
| --- | --- |
| `auth` | Cadastro de contas, login, tokens JWT, validação de e-mail e recuperação de senha |
| `park` | Usuários do estacionamento, veículos, associações e ocorrências |
| `rh` | Operações administrativas de RH, incluindo gestão de usuários e guardas |
| `notification` | Notificações da aplicação e envio assíncrono de e-mails |
| `shared` | Recursos comuns utilizados pela aplicação |

## Tecnologias utilizadas

- Java 21;
- Spring Boot 4.0.7;
- Spring Web MVC;
- Spring Security;
- Spring Data JPA;
- Spring Modulith;
- PostgreSQL 18;
- Flyway;
- MapStruct;
- Lombok;
- Thymeleaf e Spring Mail;
- JWT com chaves RSA;
- Springdoc OpenAPI e Swagger UI;
- JUnit e Testcontainers;
- Maven;
- Docker Compose.

## Requisitos

Antes de executar o projeto, tenha instalado:

- JDK 21;
- Docker e Docker Compose;
- Git.

O projeto contém o Maven Wrapper, portanto não é necessário instalar o Maven globalmente.

## Configuração

Na raiz do projeto, crie o arquivo `.env` a partir do exemplo fornecido:

```bash
cp .env.example .env
```

No Windows PowerShell:

```powershell
Copy-Item .env.example .env
```

Configure as variáveis:

```dotenv
DB_NAME=
DB_USER=
DB_PASSWORD=
JWT_PRIVATE_KEY=
JWT_PUBLIC_KEY=
MAIL_USERNAME=
MAIL_PASSWORD=
```

As chaves JWT devem formar um par RSA válido. As credenciais de e-mail são utilizadas pelos fluxos de validação de conta, recuperação de senha e notificações.

> Nunca versione o arquivo `.env` nem publique senhas, tokens ou chaves privadas.

## Como executar

### 1. Inicie o banco de dados

```bash
docker compose up -d
```

O Docker Compose inicia o PostgreSQL 18 na porta `5432`.

### 2. Inicie a API

Linux ou macOS:

```bash
./mvnw spring-boot:run
```

Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

A aplicação será iniciada em:

```text
http://localhost:8081
```

Durante a inicialização, o Flyway aplica as migrations necessárias nos schemas da aplicação.

## Autenticação

A API utiliza autenticação stateless com JWT. Nas rotas protegidas, envie o token no cabeçalho:

```http
Authorization: Bearer <token>
```

Os perfis de acesso existentes são:

- `ROLE_PARK`: colaboradores e visitantes;
- `ROLE_GUARD`: guardas do estacionamento;
- `ROLE_RH`: profissionais de RH;
- `ROLE_ADMIN`: administradores.

## Principais recursos da API

### Autenticação — `/auth`

- seleção de perfil por e-mail;
- login;
- cadastro de colaboradores e visitantes;
- validação de e-mail;
- recuperação de senha.

### Usuários do estacionamento — `/park`

- consulta do próprio perfil;
- atualização de dados de colaborador ou visitante.

### Veículos — `/vehicle`

- cadastro e atualização de veículos;
- consulta paginada e filtrada;
- consulta dos veículos do usuário autenticado;
- solicitação, confirmação e remoção de associações.

### Ocorrências — `/occurrence`

- consulta paginada e filtrada de ocorrências;
- consulta das ocorrências do usuário autenticado;
- registro e atualização de advertências;
- registro e atualização de acidentes de trânsito;
- registro e atualização de estacionamento irregular.

### Notificações — `/notification`

- consulta paginada das próprias notificações;
- exclusão de notificações.

### Recursos Humanos — `/rh`

- cadastro e atualização de usuários de RH;
- consulta e gerenciamento de usuários;
- ativação e desativação de contas;
- cadastro e atualização de guardas.

## Documentação da API

Com a aplicação em execução, a documentação OpenAPI pode ser acessada em:

```text
http://localhost:8081/swagger-ui/index.html
http://localhost:8081/v3/api-docs
```

Na configuração atual, essas rotas são protegidas e exigem o perfil `ROLE_ADMIN`.

## Testes

Para executar os testes:

Linux ou macOS:

```bash
./mvnw test
```

Windows:

```powershell
.\mvnw.cmd test
```

Os testes de integração utilizam Testcontainers com PostgreSQL 18. Por isso, o Docker deve estar em execução.

## Banco de dados

O esquema do banco não é gerado automaticamente pelo Hibernate. Todas as alterações estruturais são versionadas com Flyway em:

```text
src/main/resources/db/migration
```

Os schemas utilizados são `infra`, `auth`, `rh`, `park` e `notification`.

## Finalidade educacional

O WEGpark foi desenvolvido para praticar conceitos de APIs REST, arquitetura modular, segurança, persistência, eventos, testes automatizados e containerização. Todo o projeto deve ser entendido como parte de uma jornada de aprendizagem, sem vínculo com um produto oficial ou ambiente de produção da WEG.

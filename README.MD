
Para encerrar o banco local:

```bash
docker compose down
```

## Autenticação e autorização

Os endpoints protegidos esperam o JWT no cabeçalho HTTP:

```http
Authorization: Bearer <token>
```

Os principais perfis de acesso são:

| Perfil | Responsabilidade geral |
| --- | --- |
| `ROLE_PARK` | Colaboradores e visitantes que utilizam o estacionamento |
| `ROLE_GUARD` | Guardas responsáveis por veículos e ocorrências |
| `ROLE_RH` | Equipe responsável pela gestão de usuários |
| `ROLE_ADMIN` | Administração geral da aplicação |

As permissões efetivas são definidas na configuração do Spring Security. Contas inativas ou inexistentes são rejeitadas mesmo quando o token apresentado é válido.

## Principais endpoints

A tabela apresenta uma visão geral dos recursos. Os corpos das requisições, respostas, filtros e regras de acesso podem ser consultados na documentação OpenAPI da aplicação.

| Método | Rota | Descrição |
| --- | --- | --- |
| `POST` | `/auth` | Consulta os perfis associados a um e-mail |
| `POST` | `/auth/login` | Autentica um usuário |
| `POST` | `/auth/register/collaborator` | Cadastra uma conta de colaborador |
| `POST` | `/auth/register/visitor` | Cadastra uma conta de visitante |
| `GET` | `/auth/validate-email/{token}` | Valida o e-mail de uma conta |
| `POST` | `/auth/validate-email/resend` | Reenvia a validação de e-mail |
| `POST` | `/auth/reset-password/check-email` | Inicia a recuperação de senha |
| `POST` | `/auth/reset-password/check-email/answer` | Valida o código de recuperação |
| `PATCH` | `/auth/reset-password` | Define a nova senha |
| `GET` | `/park/profile` | Consulta o perfil do usuário autenticado |
| `PATCH` | `/park/profile/collaborator` | Atualiza o perfil de colaborador |
| `PATCH` | `/park/profile/visitor` | Atualiza o perfil de visitante |
| `POST` | `/vehicle` | Cadastra um veículo |
| `GET` | `/vehicle` | Consulta veículos com paginação e filtros |
| `GET` | `/vehicle/me` | Consulta os veículos do usuário autenticado |
| `PUT` | `/vehicle/{uuid}` | Atualiza um veículo |
| `POST` | `/vehicle/associate/{uuidNotification}` | Confirma uma associação de veículo |
| `POST` | `/vehicle/associate/notification` | Solicita uma associação de veículo |
| `POST` | `/vehicle/associate/disable/{vehicleUuid}` | Remove uma associação de veículo |
| `GET` | `/occurrence` | Consulta ocorrências com paginação e filtros |
| `GET` | `/occurrence/{uuid}` | Consulta uma ocorrência específica |
| `GET` | `/occurrence/me` | Consulta ocorrências do usuário autenticado |
| `POST` | `/occurrence/warning` | Registra uma advertência |
| `POST` | `/occurrence/traffic-accident` | Registra um acidente de trânsito |
| `POST` | `/occurrence/illegal-parking` | Registra um estacionamento irregular |
| `PUT` | `/occurrence/warning/{uuid}` | Atualiza uma advertência |
| `PUT` | `/occurrence/traffic-accident/{uuid}` | Atualiza um acidente de trânsito |
| `PUT` | `/occurrence/illegal-parking/{uuid}` | Atualiza um estacionamento irregular |
| `GET` | `/notification` | Consulta as notificações do usuário autenticado |
| `DELETE` | `/notification/{uuid}` | Exclui uma notificação do usuário autenticado |
| `POST` | `/rh` | Cadastra um usuário de RH |
| `GET` | `/rh` | Consulta usuários com paginação e filtros |
| `GET` | `/rh/me` | Consulta o perfil de RH autenticado |
| `PATCH` | `/rh/me` | Atualiza o próprio perfil de RH |
| `GET` | `/rh/user/{userUuid}` | Consulta um usuário gerenciado pelo RH |
| `PUT` | `/rh/user/{userUuid}` | Atualiza um usuário gerenciado pelo RH |
| `POST` | `/rh/user/{userUuid}/desactivate` | Ativa ou desativa um usuário |
| `POST` | `/rh/guard` | Cadastra um guarda |
| `PUT` | `/rh/guard/{guardUuid}` | Atualiza um guarda |
| `PUT` | `/rh/{rhUuid}` | Atualiza um usuário de RH |

## OpenAPI e Swagger UI

Com a aplicação em execução, a especificação e a interface do Swagger ficam disponíveis nas rotas padrão:

```text
http://localhost:8081/v3/api-docs
http://localhost:8081/swagger-ui/index.html
```

De acordo com a configuração de segurança atual, essas rotas exigem autenticação com o perfil `ROLE_ADMIN`.

## Testes

Execute a suíte de testes com:

Linux ou macOS:

```bash
./mvnw test
```

Windows:

```powershell
.\mvnw.cmd test
```

Os testes incluem unidades de serviço e fluxos de integração. Os testes integrados utilizam Testcontainers com PostgreSQL 18, portanto o Docker deve estar em execução.

## Banco de dados

O Hibernate está configurado para não criar nem alterar o schema automaticamente. A evolução do banco é controlada exclusivamente pelas migrations localizadas em:

```text
src/main/resources/db/migration
```

As migrations trabalham com os schemas `infra`, `auth`, `rh`, `park` e `notification`.

## Observação final

O WEGpark foi criado como exercício prático para estudar desenvolvimento de APIs, segurança, persistência, arquitetura modular, eventos, testes e containerização no ecossistema Spring. Seu conteúdo deve ser entendido como material de aprendizagem, e não como uma solução oficial ou pronta para produção.
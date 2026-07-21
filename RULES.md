# Manual de Arquitetura, Padronização de Banco de Dados e Processos de Desenvolvimento

Este documento consolida as diretrizes técnicas, arquiteturais e de processos adotadas no desenvolvimento do ecossistema backend. Todos os desenvolvedores devem seguir estritamente estas definições para garantir a consistência, escalabilidade e qualidade do código.
---

## Stack

* **DataBase**: Postgres - V18
* **LP**: Java - V21
* **Framework:** Spring - V4.0.7
* **Containering:** Docker
* **Versioning:** Git - GitHub - GitFlow
* **SQL versioning:** Flyway Migration

---

## Regras de Escrita

* **Nome de Pacote**: Escrever sempre no singular. Ex: ~~mappers~~ -> mapper
* **Nomes das branchs de feature**: Além do nome padrão para identificação sempre colocar um # como o número da feature. Ex: Feature/#1 texto

---

## 1. Arquitetura do Sistema

O projeto adota uma abordagem híbrida que combina o melhor da organização modular no nível macro com o padrão clássico e de fácil manutenção no nível micro.

```
┌──────────────────────────────────────────────────────────┐
│                   MONOLITO MODULAR (Macro)               │
│                                                          │
│   ┌──────────────┐      Eventos/APIs     ┌───────────┐   │
│   │   Módulo A   │ <───────────────────> │ Módulo B  │   │
│   └──────────────┘                       └───────────┘   │
│          │                                     │         │
│          ▼ (Micro)                             ▼ (Micro) │
│     MVC (Internal)                        MVC (Internal) │
└──────────────────────────────────────────────────────────┘
```

### 1.1. Macro-Arquitetura: Monolito Modular
Para evitar o acoplamento excessivo comum em monolitos tradicionais ("lama asfáltica") e a complexidade operacional precoce de microsserviços, adotamos o **Monolito Modular**.
* **Isolamento:** Cada módulo de negócio deve ser tratado como uma unidade lógica independente.
* **Comunicação:** A comunicação direta (importação de classes) entre diferentes módulos é estritamente proibida, com exceção de interfaces públicas ou contratos explicitamente expostos no nível raiz do módulo. A comunicação recomendada entre módulos deve ser assíncrona, baseada em **Eventos** (Event-Driven).

### 1.2. Micro-Arquitetura: MVC (Model-View-Controller)
Dentro de cada módulo, a arquitetura interna segue o padrão **MVC**, adaptado para o contexto de APIs REST:
* **Model (Domínio/Entidades):** Representa o estado e as regras de negócio puras do sistema.
* **View (DTOs/Serialização):** APIs REST não possuem "telas" HTML tradicionais. A nossa camada "View" é representada pelas classes de **DTO (Data Transfer Objects)** que definem as estruturas de dados enviadas e recebidas via JSON.
* **Controller:** Controla o fluxo de entrada e saída, traduz as requisições HTTP e delega as operações para a camada de serviços.

---

## 2. Estrutura de Pastas e Encapsulamento

A estrutura de pacotes do projeto foi desenhada para reforçar o isolamento do módulo por meio do conceito de visibilidade.

### 2.1. Árvore de Diretórios Padrão

```text
(nome-do-modulo)/                  # Pacote raiz do módulo (Ex: modulo-usuario)
│
├── EventoDominioX.java            # Evento disparado por este módulo (visível externamente)
├── EventoDominioY.java            # Outro evento do módulo
├── ModuloConfiguration.java       # Configurações globais/públicas do módulo
│
└── internal/                      # Subpasta obrigatória de isolamento
    ├── controller/                # Controladores HTTP (REST Endpoints)
    ├── dto/                       # Objetos de Transferência de Dados (Requests/Responses)
    ├── app/                       # Camada de lógica de negócio e orquestração
    ├── domain/                    # Entidades, Enums e Objetos de Valor (Domain Model)
    ├── infra/                     # Repositories, integrações externas e adapters de frameworks
    └── listener/                  # Consumidores (listeners) de eventos do sistema
```

### 2.2. Diretrizes de Visibilidade e Responsabilidade

> ⚠️ **REGRA DE OURO (Isolamento de Código):** 
> Nada que esteja dentro do pacote `internal/` de um módulo pode ser importado por outro módulo. Se o `Módulo B` precisa de uma informação do `Módulo A`, ele deve assinar um Evento publicado pelo `Módulo A` ou consumir uma API pública exposta através do raiz de `Módulo A`.

#### Detalhamento das Camadas Internas:

1. **Raiz do Módulo:**
   * Contém as definições de eventos disparados pelo módulo e configurações de inicialização. É a única área cujas classes podem ser visíveis externamente caso necessário.
2. **`internal`:**
   * Diretório que agrupa todo o código privado. Serve como barreira conceitual (e, onde possível, física, usando visibilidade de pacote da linguagem) para evitar acoplamento indesejado.
3. **`internal/controller`:**
   * Ponto de entrada das requisições HTTP. Responsável apenas por validar dados básicos da requisição, mapear rotas, chamar os serviços correspondentes e retornar a resposta adequada. Nenhum tipo de lógica de negócio deve residir aqui.
4. **`internal/dto`:**
   * Classes simples de dados (Records ou POJOs planos). Utilizadas para definir os contratos de entrada (Request payload) e saída (Response payload). Garantem que mudanças internas em entidades de banco de dados não quebrem os contratos de integração com o frontend.
5. **`internal/service`:**
   * O coração do módulo. Orquestra a execução da lógica de negócio, interage com os repositórios da camada de infraestrutura e gerencia transações de banco de dados.
6. **`internal/domain`:**
   * Contém as entidades que mapeiam o banco de dados (e entidades ricas de domínio) e enums. Deve focar na consistência interna dos dados de negócio.
7. **`internal/infra`:**
   * Concentra os acoplamentos tecnológicos. Aqui ficam as implementações de acesso a banco de dados (Repositories/DAO), arquivos de configuração de frameworks, clientes de APIs externas (SDKs, WebClients), integradores de mensageria, etc.
8. **`internal/listener`:**
   * Classes responsáveis por escutar e processar eventos disparados tanto internamente quanto por outros módulos (como eventos de mensageria local ou tópicos de brokers externos).

---

## 3. Versionamento de Banco de Dados (Migrations)

Utilizamos ferramentas de migração de banco de dados (como Flyway ou similares) para garantir que todas as alterações de esquema sejam rastreáveis, seguras e executadas de forma automática em todos os ambientes.

### 3.1. Padrão de Nomenclatura de Arquivos
Todos os scripts SQL de migração de banco de dados devem seguir rigidamente a convenção abaixo:

```text
V<Timestamp>__<descricao_da_acao>.sql
```

#### Exemplo Prático:
`V160720261004__create_users_table.sql`

#### Anatomia do Nome:

| Parte | Elemento | Descrição / Regra                                                                                                               | Exemplo              |
| :--- | :--- |:--------------------------------------------------------------------------------------------------------------------------------|:---------------------|
| **Prefixo** | `V` | Prefixo obrigatório para migrações com versão estática.                                                                         | `V`                  |
| **Timestamp** | `20260715100400` | Timestamp com precisão de segundo representando o exato momento de criação. <br>`YYYYMMDDHHMMSS` (Ano, Mês, Dia, Hora, Minuto). | `202607151004`       |
| **Separador** | `__` | **Dois underlines obrigatoriamente.** É o delimitador utilizado pelo motor de migração.                                         | `__`                 |
| **Descrição** | `create_users_table` | Descrição sucinta em inglês ou português utilizando letras minúsculas e separada por underlines (`snake_case`).                 | `create_users_table` |
| **Extensão** | `.sql` | Extensão padrão do arquivo (ou `.java` para migrações programáticas complexas).                                                 | `.sql`               |

### 3.2. Melhores Práticas para Migrations
1. **Imutabilidade:** Nunca altere um arquivo de migração que já tenha sido aplicado em produção ou homologação. Se precisar corrigir algo, crie uma nova migração (ex: `V20260715110000__add_missing_column_to_users.sql`).
2. **Idempotência:** Garanta que suas queries rodem de forma segura. Contudo, evite lógica excessivamente condicional em migrations versionadas se o banco possuir controle rígido de histórico.
3. **Evitar DDL e DML juntos:** Procure não misturar comandos de estrutura (Ex: `CREATE TABLE`, `ALTER TABLE`) com comandos de inserção de dados em larga escala (Ex: `INSERT INTO`) no mesmo arquivo de migração para evitar problemas com transações parcialmente executadas em bancos que não suportam DDL transacional.

---

## 4. Gerenciamento de Configurações e Variáveis de Ambiente

Para garantir a portabilidade do software entre diferentes ambientes (Desenvolvimento, Homologação e Produção) sem a necessidade de alterar o código-fonte, adotamos o padrão de configuração baseado em **Variáveis de Ambiente** injetadas por arquivos `.env`.

### 4.1. Diretrizes para o Uso do `.env`

1. **Segurança em Primeiro Lugar (`.gitignore`):**
   * **NUNCA** envie o arquivo `.env` contendo credenciais reais ou chaves secretas para o repositório Git. 
   * O arquivo `.env` deve ser listado obrigatoriamente no arquivo `.gitignore` do projeto:
     ```text
     # No seu arquivo .gitignore:
     .env
     .env.local
     ```

2. **O Arquivo de Exemplo (`.env.example`):**
   * Para instruir novos desenvolvedores sobre quais variáveis o sistema precisa para funcionar, deve-se manter um arquivo modelo chamado `.env.example` versionado no Git.
   * Este arquivo modelo contém apenas as chaves das variáveis, com valores de exemplo fictícios ou vazios:
     ```env
     # .env.example
     DATABASE_URL=jdbc:postgresql://localhost:5432/db_nome
     DATABASE_USER=seu_usuario
     DATABASE_PASSWORD=sua_senha
     API_JWT_SECRET=coloque_uma_chave_segura_aqui
     ```

3. **Convenção de Nomenclatura:**
   * Todas as variáveis de ambiente devem ser escritas em **letras maiúsculas separadas por underline (`UPPER_SNAKE_CASE`)**.
   * Em monolitos modulares, é recomendável adicionar o nome do módulo como prefixo em variáveis que sejam específicas da infraestrutura daquele módulo:
     ```env
     USER_MOD_DB_URL=jdbc:postgresql://localhost:5432/users_db
     PAYMENT_MOD_GATEWAY_KEY=api_key_xyz123
     ```

4. **Injeção em Tempo de Execução:**
   * Em produção, as variáveis não virão de um arquivo físico `.env`, mas sim do próprio ambiente de hospedagem (Docker, Kubernetes, AWS, Heroku, etc.). O backend deve estar preparado para ler as variáveis diretamente do sistema operacional.

---

## 5. Metodologia de Organização de Repositório Git (GitFlow)

Adotamos o fluxo de ramificação **GitFlow** para gerenciar de forma previsível as entregas, correções de bugs e ciclos de releases de software.

```
                  ┌───────── Master (Produção) ◄───────────────┐
                  │                                            │
                  │              ▲ (Hotfix)                    │ (Release / Hotfix)
                  ▼              │                             │
    ┌────────► Release ──────────┴─────────────────────────────┤
    │                                                          │
    │             ▲ (Merge de Features / Bugfixes)             │
    │             │                                            │
    │   ┌─────► Develop ───────────────────────────────────────┘
    │   │         ▲
    │   │         │ (Feature / Bugfix)
    │   │         ▼
    └───┴─── Feature / Bugfix
```

### 5.1. Principais Branches e Regras de Proteção

| Branch | Origem recomendada | Destino de Integration | Permissões / Regras de Acesso |
| :--- | :--- | :--- | :--- |
| `master` (ou `main`) | `release` ou `hotfix` | *Nenhum (ponto final)* | **Bloqueada para push direto.** Recebe apenas merges formais de releases ou hotfixes testados. Sempre contém tag de versão (Ex: `v1.2.0`). Representa o estado em produção. |
| `release` | `develop` | `master` e `develop` | **Bloqueada para push direto.** Contém o código candidato a produção. Passa por testes de regressão e correção de bugs finais. |
| `develop` | - | `release` | **Bloqueada para push direto.** É a branch de integração contínua do desenvolvimento. Todas as features prontas são integradas aqui. |
| `feature/` | `develop` | `develop` | **Livre para escrita dos desenvolvedores.** Utilizada para codificar novas funcionalidades. O merge de volta para `develop` é feito estritamente via Pull Request (PR) com revisão de código. |
| `bugfix/` | `develop` ou `release` | `develop` ou `release` | **Livre para escrita.** Similar à branch de feature, mas com foco exclusivo na resolução de defeitos identificados durante o ciclo de testes em desenvolvimento/homologação. |
| `hotfix/` | `master` | `master` e `develop` | **Livre para escrita de engenheiros autorizados.** Branch temporária de alta prioridade para sanar problemas críticos diretamente em produção. |

---

### 5.2. Comandos Principais e Fluxo de Trabalho

#### Inicialização do Repositório (Obrigatório)
Sempre que clonar o repositório ou precisar reconfigurar o GitFlow localmente, execute o seguinte comando e certifique-se de preencher as perguntas com os padrões definidos abaixo:

```bash
git flow init
```

##### ⚠️ CONFIGURAÇÃO REQUERIDA (Durante o `git flow init`):
* **Branch para releases de produção:** `master` (ou `main`)
* **Branch para integração/desenvolvimento:** `develop`
* **Prefixo de ramificação de Feature:** `feature/`
* **Prefixo de ramificação de Bugfix:** `bugfix/`
* **Prefixo de ramificação de Release:** `release/`
* **Prefixo de ramificação de Hotfix:** `hotfix/`
* **Prefixo de ramificação de Support:** `support/`
* **Prefixo de tag de versão:** `v` (Ex: `v1.0.0`)

---

#### Ciclo de Desenvolvimento de Novas Funcionalidades (Feature)

1. **Iniciar a Feature:**
   Cria a branch local `feature/nome-da-feature` a partir da `develop` mais atualizada.
   ```bash
   git flow feature start nome-da-feature
   ```

2. **Publicar a Feature:**
   Sobe a branch para o servidor de origem remoto para que seu progresso esteja seguro e visível.
   ```bash
   git flow feature publish nome-da-feature
   ```

3. **Abrir Pull Request (PR):**
   > 📢 **IMPORTANTE:** Sempre que publicar uma branch de feature/bugfix, você deve acessar a interface web do repositório (GitHub, GitLab, Azure DevOps) e **abrir uma Pull Request (PR) para a branch `develop`**. O merge definitivo da funcionalidade na `develop` ocorrerá via ferramenta após a aprovação de code review e testes integrados.

---

#### Ciclo de Correção de Defeitos em Desenvolvimento ou Homologação (Bugfix)

1. **Iniciar o Bugfix:**
   Cria a branch local `bugfix/nome-do-bug` a partir da `develop` (ou da release correspondente).
   ```bash
   git flow bugfix start nome-do-bug
   ```

2. **Publicar o Bugfix:**
   Envia a branch de correção para o servidor remoto.
   ```bash
   git flow bugfix publish nome-do-bug
   ```

---

#### Ciclo de Preparação para Lançamento (Release)

1. **Iniciar a Release:**
   Reúne todo o código acumulado na `develop` em uma ramificação de homologação sob uma versão específica.
   ```bash
   git flow release start 1.2.0
   ```

2. **Publicar a Release:**
   Garante que a equipe de QA (Quality Assurance) possa baixar o código e realizar os testes de fumaça e homologação no ambiente correspondente.
   ```bash
   git flow release publish 1.2.0
   ```

3. **Finalizar a Release (Remoto):**
   Após a validação, a release deve ser finalizada, fazendo o merge para a `master`, gerando a respectiva tag de versão (Ex: `v1.2.0`) e atualizando a `develop` com quaisquer correções de última hora feitas no ambiente de homologação. (Recomenda-se realizar este merge via PR/Pipeline de CI/CD para manter a integridade das branches protegidas).

---

#### Ciclo de Correções Críticas Emergenciais em Produção (Hotfix)

1. **Iniciar o Hotfix:**
   Cria uma branch a partir da `master` em produção para consertar um bug crítico que está afetando usuários finais instantaneamente.
   ```bash
   git flow hotfix start 1.0.1
   ```

2. **Publicar o Hotfix:**
   Envia a correção urgente para o servidor para revisão.
   ```bash
   git flow hotfix publish 1.0.1
   ```

3. **Conclusão:**
   Semelhante à release, o hotfix concluído é incorporado diretamente de volta à `master` (gerando nova tag, ex: `v1.0.1`) e também mesclado de volta para a `develop` para assegurar que a correção não se perca no próximo ciclo regular de deploy.
---
## 6. Orquestração Local de Serviços (Docker Compose)
Utilizamos o Docker Compose para padronizar e automatizar a inicialização da infraestrutura local de desenvolvimento (como bancos de dados, corretores de mensageria e caches) necessária para o funcionamento do backend.
5.1. Comandos Essenciais de Operação
Iniciar os Serviços (`docker compose up`)
Este comando lê o arquivo `docker-compose.yml` no diretório atual e inicializa todos os containers configurados.
```bash

# Inicia todos os containers no modo interativo (mostra os logs no terminal atual)
docker compose up

# MODO RECOMENDADO: Inicia os containers em segundo plano (Detached Mode)
# Libera o seu terminal atual para que você possa continuar trabalhando.
docker compose up -d
```
Parar e Limpar os Serviços (`docker compose down`)
Este comando para a execução dos containers e limpa os recursos de rede criados para o ambiente. Ele é crucial para liberar memória e encerrar as conexões de rede locais de forma saudável.
```bash
# Para e remove os containers e as redes internas locais criadas pelo Docker Compose
docker compose down

# REMOÇÃO COMPLETA (Containers, Redes e Volumes de Dados):
# Use esta opção quando quiser redefinir o banco de dados local para o estado original,
# apagando todos os registros e volumes de dados salvos localmente.
docker compose down -v
```

# AGENTS.md

> **Instrução obrigatória:** antes de ler este arquivo ou executar qualquer tarefa no repositório, leia integralmente o arquivo `RULES.md`.
>
> Esta obrigação também se aplica a agentes de inteligência artificial, assistentes de código, automações e quaisquer outras ferramentas capazes de analisar ou modificar o projeto.

## 1. Objetivo deste arquivo

Este arquivo define as regras obrigatórias para agentes de IA que analisam, sugerem ou implementam alterações neste projeto.

As instruções aqui descritas devem ser seguidas em conjunto com o `RULES.md`. Em caso de conflito ou dúvida, interrompa a execução, explique o conflito encontrado e solicite orientação antes de continuar.

## 2. Stack do projeto

- **Banco de dados:** PostgreSQL 18
- **Linguagem:** Java 21
- **Framework:** Spring 4.0.7
- **Containerização:** Docker
- **Versionamento:** Git, GitHub e GitFlow
- **Versionamento de banco de dados:** Flyway Migrations

Antes de propor ou escrever qualquer código, analise o contexto da aplicação e compreenda:

- a arquitetura e a organização do projeto;
- o código relacionado à tarefa;
- as ferramentas e bibliotecas utilizadas;
- o fluxo completo da funcionalidade;
- os padrões já adotados no repositório.

## 3. Leitura e análise obrigatórias antes de qualquer alteração

Antes de escrever, alterar ou sugerir código:

1. Leia integralmente o `RULES.md`.
2. Analise o `pom.xml` e identifique todas as bibliotecas, plugins e dependências atualmente presentes.
3. Analise como essas dependências são utilizadas no código existente.
4. Examine os módulos, pacotes, convenções, nomenclaturas e padrões relacionados à tarefa.
5. Analise o fluxo completo da funcionalidade, desde a entrada até a resposta final.
6. Verifique controllers, services, mappers, repositories, entidades, DTOs, enums, exceções, configurações e testes relacionados.
7. Confirme que a solução proposta utiliza os recursos e padrões já adotados pela aplicação.

Não introduza uma biblioteca, padrão, abstração ou ferramenta nova quando o projeto já possuir uma solução adequada para o mesmo problema.

## 4. Proibição de alterações sem autorização

**Não edite nenhum arquivo sem autorização prévia e explícita.**

Antes de modificar qualquer coisa:

1. apresente a análise realizada;
2. explique o problema ou a necessidade identificada;
3. descreva os arquivos que precisariam ser alterados;
4. apresente a solução proposta;
5. solicite permissão para executar a alteração.

A autorização deve ser específica para o escopo apresentado. Não interprete uma autorização limitada como permissão para realizar alterações adicionais.

Caso identifique melhorias, correções ou refatorações fora do escopo solicitado, apenas informe o que encontrou, explique o impacto e aguarde a decisão do responsável pelo projeto.

## 5. Arquitetura de monólito modular

O projeto segue a arquitetura de **monólito modular**.

Evite ao máximo compartilhar recursos diretamente entre módulos. A comunicação entre módulos deve ocorrer, preferencialmente, por meio das ferramentas, mecanismos e padrões do Spring que já estejam sendo utilizados no projeto.

Não crie acoplamentos diretos entre módulos apenas para reutilizar classes.

### 5.1 Recursos compartilhados dentro de um módulo

Caso um DTO, enum ou outro recurso precise ser reutilizado dentro do mesmo módulo para evitar arquivos idênticos:

1. verifique se o módulo já possui uma pasta `shared` em sua raiz;
2. se não possuir, proponha a criação dessa pasta antes de alterá-lo;
3. organize os recursos compartilhados por tipo, criando ao menos pastas como:
   - `shared/dto` para DTOs;
   - `shared/enum` para enums;
   - outras subdivisões equivalentes quando necessárias.

A estrutura rígida definida no `RULES.md` não precisa ser seguida integralmente dentro de `shared`, mas a organização deve permanecer clara, consistente e compatível com o que já é feito no projeto.

Antes de criar qualquer estrutura compartilhada, analise exemplos existentes no repositório e siga o padrão predominante.

## 6. Escopo da tarefa

Implemente somente o que foi solicitado.

Evite criar código adicional, corrigir problemas não solicitados ou substituir recursos existentes sem autorização.

Exemplo: se a tarefa solicitar a criação de um controller, crie o controller e evite alterar o service. Mesmo assim, analise todo o fluxo para verificar se o controller funcionará corretamente.

Caso identifique que um service, mapper, repository ou outro componente prejudica o retorno ou impede o funcionamento correto da tarefa:

1. informe o problema encontrado;
2. explique seu impacto;
3. apresente uma solução;
4. aguarde a decisão do responsável antes de alterar esse componente.

Não amplie silenciosamente o escopo da tarefa.

## 7. Revisão obrigatória do próprio código

Sempre revise o próprio código antes de apresentá-lo ou considerá-lo concluído.

Durante a revisão, verifique se:

- a solução faz sentido no contexto da aplicação;
- o código está de acordo com os padrões existentes;
- a implementação não está excessivamente diferente de códigos equivalentes do projeto;
- não houve overengineering;
- não foram criadas abstrações desnecessárias;
- não existe código repetitivo ou boilerplate evitável;
- a solução não introduz acoplamento desnecessário;
- os tipos, nulabilidade, retornos e tratamentos de erro estão corretos;
- o fluxo completo funciona de maneira coerente.

A solução mais sofisticada nem sempre é a melhor. Prefira a alternativa mais simples que respeite a arquitetura, os padrões do projeto e os requisitos da tarefa.

## 8. Análise completa do fluxo da funcionalidade

Analise sempre todo o processo pelo qual a funcionalidade passará.

Não avalie um arquivo isoladamente. Verifique, quando aplicável:

- entrada da requisição;
- validação;
- conversão de dados;
- regras de negócio;
- persistência;
- mapeamento;
- serialização;
- resposta do controller;
- tratamento de exceções;
- efeitos em outros módulos;
- testes relacionados.

Exemplo: ao criar ou revisar um controller, confira se o service produz o resultado esperado, se o mapper gera o DTO correto e se a resposta final corresponde ao contrato da API.

Caso algum ponto fora do escopo precise ser corrigido para que a funcionalidade opere corretamente, não o altere sem autorização. Apresente o diagnóstico e a solução proposta.

## 9. Contratos HTTP e tipos de retorno

Verifique sempre se o tipo de retorno e o status HTTP estão corretos para a operação.

Exemplos:

- `200 OK` para consultas ou operações concluídas com resposta;
- `201 Created` para criação de recursos;
- `204 No Content` quando a operação é concluída sem corpo de resposta;
- demais códigos conforme o contrato já adotado pela aplicação.

Não escolha códigos de status apenas por convenção genérica. Analise os controllers existentes e preserve a consistência do projeto.

Também verifique:

- o tipo genérico de `ResponseEntity`, quando utilizado;
- o corpo da resposta;
- cabeçalhos necessários;
- compatibilidade com clientes existentes;
- comportamento em casos de erro.

## 10. Exceções e tratamento de erros

Nunca lance exceções genéricas, como `RuntimeException`, quando o caso puder ser representado por uma exceção personalizada.

Sempre que encontrar uma exceção unchecked:

1. verifique se já existe uma exceção personalizada adequada para o caso;
2. avalie sua adequação pelo nome, contexto e uso no projeto;
3. se ela já existir, utilize-a;
4. quando houver uma operação que precise converter uma exceção técnica para a exceção de domínio ou aplicação já existente, utilize um bloco `try/catch` e lance a exceção personalizada no `catch`;
5. se não existir uma exceção personalizada adequada, proponha a criação de uma dentro do módulo em que o erro estiver inserido;
6. somente crie essa exceção após obter autorização para alterar os arquivos envolvidos.

Não trate localmente uma exceção de forma que impeça o tratamento centralizado esperado pela aplicação.

Essas regras não devem ser interpretadas como incorretas pelo agente. O projeto pode ainda não possuir um `GlobalExceptionHandler`, ou o agente pode não ter identificado sua existência. Caso ainda não exista, considere que ele poderá ser criado futuramente. Caso exista, compreenda que exceções unchecked personalizadas podem ser lançadas para que sejam tratadas por ele.

Nunca silencie exceções. Não utilize blocos `catch` vazios e não substitua erros por retornos aparentemente válidos sem que esse seja o comportamento explicitamente definido pelo projeto.

## 11. Boilerplate, repetição e abstrações

Evite código boilerplate, repetitivo e desnecessariamente extenso.

Ao mesmo tempo, não crie abstrações complexas apenas para eliminar uma pequena repetição.

Caso uma situação exija muitos arquivos, camadas ou abstrações, mas possa ser resolvida de maneira mais prática com uma repetição simples e controlada:

1. apresente as alternativas;
2. explique os custos e benefícios de cada uma;
3. destaque o trade-off entre repetição e abstração;
4. aguarde a decisão do responsável pelo projeto.

Não tome sozinho decisões arquiteturais relevantes baseadas apenas em preferência estética.

## 12. Persistência, JPA, Hibernate e consultas

Evite problemas de desempenho, especialmente consultas N+1.

Analise o comportamento real das consultas geradas pelo JPA e pelo Hibernate. Não suponha que métodos derivados são sempre a melhor solução.

Evite estratégias que façam diversas chamadas ao banco quando o problema puder ser resolvido com uma consulta única e bem definida.

Quando uma consulta manual, JPQL, Criteria API, native query, projeção ou estratégia equivalente parecer mais adequada:

1. explique por que os métodos derivados ou o carregamento padrão não são suficientes;
2. apresente a consulta ou abordagem proposta;
3. informe possíveis impactos em desempenho, manutenção e portabilidade;
4. aguarde a decisão do responsável antes de implementá-la.

Verifique também:

- estratégia de carregamento de relacionamentos;
- paginação;
- ordenação;
- cardinalidade;
- índices já existentes;
- quantidade esperada de registros;
- duplicidade causada por joins;
- limites transacionais;
- risco de `LazyInitializationException`;
- comportamento de operações em lote.

Não altere mappings de entidades ou estratégias de fetch sem autorização.

## 13. MapStruct

Utilize o MapStruct conforme os padrões já adotados pelo projeto.

O MapStruct pode emitir avisos sobre propriedades não mapeadas, `target = "."` ou situações semelhantes. Nem sempre todas as propriedades devem ser mapeadas, e mapear tudo explicitamente pode tornar o mapper excessivamente longo, repetitivo e difícil de manter.

Evite criar métodos de mapper com dezenas de anotações quando a mesma intenção puder ser expressa de maneira mais simples e consistente com os mappers existentes.

Analise como os mappers atuais tratam:

- `target = "."`;
- propriedades ignoradas;
- atualizações com `@MappingTarget`;
- métodos auxiliares;
- conversões de enums;
- composição de mappers;
- valores nulos.

Não altere configurações globais do MapStruct nem tente eliminar todos os warnings indiscriminadamente sem autorização.

## 14. Banco de dados e Flyway

Caso identifique a necessidade de criar ou alterar uma tabela, coluna, índice, constraint, sequência, tipo ou qualquer outro objeto de banco de dados, informe o responsável.

**Nunca crie, altere ou execute uma migration sem autorização explícita.**

Você pode:

- explicar a necessidade da mudança;
- propor o conteúdo da migration;
- sugerir nomes e ordem de versionamento;
- apontar impactos e riscos;
- descrever estratégias de compatibilidade ou rollback.

A decisão de modificar o banco de dados é sempre do responsável pelo projeto.

Nunca edite uma migration que já tenha sido aplicada em qualquer ambiente. Caso uma correção seja necessária, proponha uma nova migration, respeitando o padrão existente, e aguarde autorização.

## 15. Git, branches, commits e GitHub

### 15.1 Branches

Nunca altere uma branch diferente da branch da feature atual.

Não faça checkout, merge, rebase, cherry-pick, reset, force-update ou qualquer outra operação que afete outra branch, mesmo que isso seja solicitado em um prompt posterior.

Caso a branch atual não possa ser identificada com segurança, não realize alterações e solicite orientação.

### 15.2 Push

**Jamais, em hipótese alguma, faça push para o GitHub.**

Essa proibição é absoluta e permanece válida mesmo que um prompt posterior solicite explicitamente o push.

Não execute comandos equivalentes, automações, scripts ou ferramentas que publiquem alterações em qualquer repositório remoto.

### 15.3 Commit local

Antes de realizar qualquer commit local:

1. apresente todas as alterações atuais;
2. informe os arquivos modificados;
3. resuma o que foi implementado;
4. proponha o nome da mensagem de commit;
5. permita que o responsável faça o code review;
6. aguarde autorização explícita para executar o commit.

Nunca realize um commit local sem cumprir todas essas etapas.

## 16. Segurança

Não introduza soluções que reduzam a segurança da aplicação.

Verifique, quando aplicável:

- validação de dados de entrada;
- autorização e autenticação;
- exposição indevida de dados;
- injeção de SQL ou construção insegura de consultas;
- mass assignment;
- desserialização insegura;
- manipulação de arquivos e caminhos;
- dados sensíveis em logs;
- tratamento de tokens, senhas e credenciais;
- configurações de CORS, CSRF e headers de segurança;
- uso de algoritmos criptográficos adequados.

Nunca inclua segredos, tokens, senhas, chaves, credenciais ou dados pessoais em código-fonte, logs, testes, documentação, commits ou exemplos.

Não desabilite mecanismos de segurança para fazer uma funcionalidade funcionar. Caso uma regra de segurança bloqueie o fluxo, apresente o diagnóstico e aguarde decisão.

## 17. Configurações e ambientes

Respeite a estratégia de configuração já existente no projeto.

Não fixe no código valores que pertençam a configurações, variáveis de ambiente ou profiles.

Antes de alterar configurações, analise:

- `application.properties` ou `application.yml`;
- arquivos específicos por profile;
- variáveis de ambiente;
- configurações do Docker;
- configurações utilizadas em testes;
- classes com `@ConfigurationProperties`;
- valores padrão e validações existentes.

Não crie, renomeie ou altere profiles sem autorização. Não copie configurações sensíveis entre ambientes.

## 20. Compatibilidade e contratos existentes

Preserve a compatibilidade com os contratos atuais da aplicação, salvo quando uma mudança incompatível tiver sido explicitamente solicitada e autorizada.

Antes de alterar DTOs, endpoints, enums, eventos, estruturas persistidas ou assinaturas públicas, verifique os consumidores e impactos relacionados.

Não renomeie campos, altere formatos de data, mude valores de enums, remova endpoints ou modifique contratos de resposta sem informar os impactos e obter autorização.

## 21. Docker e ambiente de execução

Analise os arquivos Docker e o ambiente de execução antes de propor mudanças relacionadas a build, runtime, portas, volumes, redes ou variáveis de ambiente.

Não altere `Dockerfile`, arquivos de Compose, imagens-base ou configurações de container sem autorização.

Quando uma mudança afetar a containerização, verifique:

- compatibilidade com Java 21;
- processo de build utilizado pelo projeto;
- arquivos copiados para a imagem;
- usuário de execução;
- exposição de portas;
- health checks;
- variáveis de ambiente;
- persistência de dados;
- impacto no tempo e no tamanho da build.

## 22. Dependências

Não adicione, remova ou atualize dependências sem autorização explícita.

Antes de propor uma dependência nova:

1. confirme que o projeto não possui recurso equivalente;
2. explique por que a implementação com recursos existentes não é adequada;
3. informe o impacto no `pom.xml`;
4. verifique compatibilidade com Java, Spring e demais bibliotecas;
5. avalie manutenção, segurança, licença e peso da dependência;
6. aguarde a decisão do responsável.

Não altere versões apenas porque existe uma versão mais recente.

## 23. Formatação, estilo e documentação

Siga os padrões de formatação, nomenclatura e documentação existentes no projeto.

Não reformate arquivos inteiros quando apenas uma pequena alteração for necessária. Evite diffs ruidosos e alterações sem relação com a tarefa.

Comentários devem explicar decisões, restrições ou comportamentos não óbvios. Não adicione comentários que apenas repitam o código.

Não crie documentação adicional não solicitada. Quando uma alteração exigir atualização de documentação existente para evitar inconsistência, informe essa necessidade e solicite autorização.

## 24. Arquivos gerados e código externo

Não edite manualmente arquivos gerados por ferramentas, código vendorizado, artefatos de build ou arquivos de terceiros.

Antes de modificar um arquivo, verifique se ele é fonte original ou resultado de geração automática.

Caso a alteração deva ocorrer no gerador, schema, template ou configuração de origem, apresente essa conclusão e aguarde autorização.

## 25. Critérios de parada

Interrompa a execução e solicite decisão do responsável quando:

- a tarefa exigir alteração não autorizada;
- houver conflito entre estas regras, o `RULES.md` e o código existente;
- a branch atual não for a branch da feature;
- for necessária uma migration;
- for necessária uma dependência nova ou atualização de versão;
- o escopo solicitado não for suficiente para a funcionalidade operar corretamente;
- houver mais de uma solução com trade-offs arquiteturais relevantes;
- for necessário alterar um contrato público;
- houver risco de perda de dados, quebra de compatibilidade ou impacto de segurança;
- não for possível validar a solução com segurança.

Não esconda incertezas e não presuma autorização.

## 26. Formato esperado antes de qualquer implementação

Antes de pedir autorização para alterar o código, apresente preferencialmente:

1. **Contexto analisado** — arquivos, módulos e fluxo examinados.
2. **Diagnóstico** — comportamento atual e problema identificado.
3. **Solução proposta** — abordagem objetiva e compatível com o projeto.
4. **Arquivos afetados** — lista exata dos arquivos que seriam criados ou modificados.
5. **Impactos e riscos** — banco de dados, contratos, desempenho, segurança e compatibilidade.
6. **Validações planejadas** — testes e comandos necessários.
7. **Pedido de autorização** — solicitação explícita para executar apenas as alterações apresentadas.

## 27. Princípio final

Analise sempre a aplicação antes de escrever qualquer coisa.

Utilize os recursos que já existem no projeto. Preserve a arquitetura, a consistência e o escopo. Não altere arquivos, banco de dados, dependências, branches ou histórico Git sem autorização específica.

Quando houver dúvida, apresente o que foi encontrado, proponha as alternativas e deixe a decisão com o responsável pelo projeto.

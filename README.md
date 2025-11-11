# Escola API

Bem-vindo à API da Escola. Este documento descreve rapidamente como executar o projeto localmente e, principalmente, documenta o novo sistema de Logs/Auditoria e suas rotas.

## Execução local (resumo)
- Perfil local: defina a variável `SPRING_PROFILES_ACTIVE=local`.
- Token/JWT: defina `API_SECURITY_TOKEN_SECRET` (qualquer valor seguro em dev).
- Banco local: por padrão `application-local.properties` aponta para PostgreSQL local (jdbc, user e senha podem ser sobrescritos por variáveis de ambiente padrão do Spring, como `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`).
- Porta padrão: 8080.

## Sistema de Logs / Auditoria
O sistema registra operações de escrita (POST/PUT/DELETE) executadas em controllers no pacote `infrastructure.web.controllers`. Cada operação registrada gera um `ActivityLog` contendo:
- `id` (Long)
- `username` (String): usuário autenticado que executou a ação (ou "SISTEMA_OU_ANONIMO" se não identificado)
- `action` (String): nome do método + caminho HTTP no momento da chamada (ex.: `createProfessor @ POST /professores`)
- `details` (String): dados resumidos do primeiro argumento do método (tipicamente o corpo `@RequestBody`) serializados em JSON
- `createdAt` (Instant): timestamp de criação do log

Implementação (principais classes):
- Entidade: `com.example.escola.domain.entities.ActivityLog`
- Repositório: `com.example.escola.application.repositories.ActivityLogRepository`
- Service: `com.example.escola.application.service.ActivityLogService`
- Specifications: `com.example.escola.application.service.LogSpecifications`
- Aspecto (AOP): `com.example.escola.infrastructure.config.aop.AuditLogAspect`
- Controller: `com.example.escola.infrastructure.web.controllers.AuditLogController`

### Autenticação
As rotas de logs são protegidas por autenticação (conforme as regras de segurança da aplicação). Envie o header de autorização apropriado (por exemplo: `Authorization: Bearer <token>`).

## Rotas de Logs
### 1) GET /logs
Retorna página de logs com filtros opcionais.

Query params:
- `username` (opcional): filtra por usuário exato.
- `acao` (opcional): filtra pelo campo `action`.
- `timeFilter` (opcional): um de `24h`, `7d`, `30d` ou `all`. Se omitido, retorna sem filtro de tempo.
- Paginação (Spring): `page` (padrão 0), `size` (padrão 20), e `sort` (campo recomendável: `createdAt,desc`).

Resposta (Page):
- `content`: lista de objetos `ActivityLogResponseDTO` (id, username, action, details, createdAt)
- `pageable`, `totalElements`, `totalPages`, etc.

Exemplos:
- `/logs?size=20&sort=createdAt,desc`
- `/logs?username=admin&timeFilter=7d&size=50`
- `/logs?acao=DELETE&timeFilter=24h&page=0&size=10&sort=createdAt,desc`

### 2) GET /logs/recent
Retorna os logs mais recentes como lista simples (sem metadados de paginação).

Query params:
- `size` (opcional): quantidade de itens. Padrão: 20. Mínimo interno: 1.

Exemplos:
- `/logs/recent` → últimos 20
- `/logs/recent?size=50` → últimos 50

Resposta (List<ActivityLogResponseDTO>):
```json
[
  {
    "id": 123,
    "username": "admin",
    "action": "createProfessor @ POST /professores",
    "details": "{...}",
    "createdAt": "2025-11-10T01:23:45.678Z"
  }
]
```

## Observações e Boas Práticas
- Evite incluir dados sensíveis no `details` (o aspecto serializa o primeiro argumento do método; se necessário, ajuste o `AuditLogAspect` para filtrar/anonimizar campos).
- Para grandes volumes, prefira `GET /logs` com paginação em vez de `/logs/recent`.
- A ordenação recomendada é por `createdAt,desc`.

## Solução de Problemas
- "403 ao acessar /logs": verifique se o token/credenciais têm permissão para o recurso.
- "Conexão com banco falhou": confira as variáveis de ambiente de datasource e se o Postgres está acessível em `application-local.properties`.
- "Swagger não abre/sem autorização": autentique-se no Swagger UI pelo botão Authorize (se configurado como Bearer) ou use uma ferramenta REST (Insomnia/Postman) adicionando o header de Authorization.

---

Se precisar de exemplos prontos de requisições (curl/Insomnia) ou quiser expandir filtros (intervalo de datas `from`/`to`, busca textual no `details`), abra uma issue ou peça nos comentários que incluímos aqui.

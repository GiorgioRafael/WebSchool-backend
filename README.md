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

## Rotas de Logs (exemplos práticos)
Abaixo exemplos práticos para cada rota de logs. Use `curl`/Insomnia/Postman — substitua `<SEU_TOKEN>` pelo token JWT válido.

### 1) GET /logs
Retorna uma página de logs com filtros opcionais.

Query params suportados:
- `username` (opcional): filtra por usuário exato.
- `acao` (opcional): filtra pelo campo `action` (substring ou chave).
- `timeFilter` (opcional): um de `24h`, `7d`, `30d` ou `all`.
- Paginação padrão: `page=0&size=20&sort=createdAt,desc`.

Exemplo básico (últimos 20 logs, ordenados por mais recentes):

```bash
curl -X GET "http://localhost:8080/logs?size=20&sort=createdAt,desc" \
  -H "Authorization: Bearer <SEU_TOKEN>" \
  -H "Accept: application/json"
```

Exemplo filtrando por usuário e intervalo de tempo (últimos 7 dias):

```bash
curl -X GET "http://localhost:8080/logs?username=coordenador&timeFilter=7d&size=50" \
  -H "Authorization: Bearer <SEU_TOKEN>" \
  -H "Accept: application/json"
```

Exemplo paginado (segunda página, 10 por página):

```bash
curl -X GET "http://localhost:8080/logs?page=1&size=10&sort=createdAt,desc" \
  -H "Authorization: Bearer <SEU_TOKEN>" \
  -H "Accept: application/json"
```

Resposta (exemplo abreviado — Page):

```json
{
  "content": [
    {
      "id": 123,
      "username": "coordenador",
      "action": "updateAluno @ PUT /alunos/123456",
      "details": "nomeCompleto: João Antigo -> João Novo; email: antigo@example.com -> novo@example.com",
      "createdAt": "2025-11-11T13:49:34.398109Z",
      "createdAtFormatted": "11-11-2025T10:49:34.398-03:00"
    }
  ],
  "pageable": { /* meta */ },
  "totalElements": 42,
  "totalPages": 3,
  "last": false,
  "size": 20,
  "number": 0
}
```

Observação: `details` para endpoints onde o service gera diffs (PUT/UPDATE) será uma string "campo: antigo -> novo"; para rotas auditadas automaticamente pelo AOP sem diff, `details` pode ser o JSON do request body.

### 2) GET /logs/recent
Retorna uma lista simples com os logs mais recentes (sem metadados de paginação).

Exemplo (últimos 20):

```bash
curl -X GET "http://localhost:8080/logs/recent" \
  -H "Authorization: Bearer <SEU_TOKEN>" \
  -H "Accept: application/json"
```

Exemplo pedindo os últimos 50:

```bash
curl -X GET "http://localhost:8080/logs/recent?size=50" \
  -H "Authorization: Bearer <SEU_TOKEN>" \
  -H "Accept: application/json"
```

Resposta (exemplo):

```json
[
  {
    "id": 130,
    "username": "admin",
    "action": "createProfessor @ POST /professores",
    "details": "{...}",
    "createdAt": "2025-11-11T12:00:00.000Z",
    "createdAtFormatted": "11-11-2025T09:00:00.000-03:00"
  },
  {
    "id": 129,
    "username": "coordenador",
    "action": "updateAluno @ PUT /alunos/123456",
    "details": "email: antigo@example.com -> novo@example.com",
    "createdAt": "2025-11-11T11:49:34.398Z"
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

Se quiser, posso também adicionar exemplos para cURL que incluem o body de um PUT/POST específico (ex.: `PUT /alunos/{matricula}` mostrando o JSON de request usado para gerar o diff) ou gerar uma collection do Postman/Insomnia com essas requisições.

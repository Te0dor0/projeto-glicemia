# ☽ Sistema de Gerenciamento de Glicemia

Sistema web para controle glicêmico com tema noturno estrelado. Registra refeições, mede glicemia antes e 2 horas após cada refeição, exibe alertas visuais e integra bidiretcionalmente com planilha Excel.

---

## Tecnologias

| Camada | Tecnologia |
|--------|-----------|
| Backend | Java 17 + Spring Boot 3.2 |
| Banco de dados | MySQL 8 |
| Segurança | Spring Security + JWT (jjwt 0.12) |
| Excel | Apache POI 5.2 |
| Frontend | HTML + CSS + JS (Vanilla) |
| Deploy | Docker + Docker Compose |

---

## Usuários do Sistema

| Usuário | Senha | Papel | Permissões |
|---------|-------|-------|------------|
| `Teo` | `REMOVED_PASSWORD` | ADMIN | Tudo: listar/editar/excluir qualquer dado, gerenciar estrelas, ver logs |
| `Lui` | `REMOVED_PASSWORD` | USER | Registrar refeições, ver seus dados, registrar medições 2H, ver estrelas |

---

## Estrutura do Projeto

```
glicemia/
├── backend/                          # Spring Boot
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/glicemia/
│       │   ├── GlicemiaApplication.java
│       │   ├── config/
│       │   │   ├── DataInitializer.java      # Cria usuários e estrelas no boot
│       │   │   └── SecurityConfig.java       # JWT + CORS + roles
│       │   ├── controller/
│       │   │   └── Controllers.java          # Auth, Dashboard, Refeições, Medições, Estrelas, Logs
│       │   ├── dto/
│       │   │   └── AllDtos.java              # Todos os DTOs de request/response
│       │   ├── entity/
│       │   │   ├── Usuario.java
│       │   │   ├── Refeicao.java             # Enum TipoRefeicao incluído
│       │   │   ├── Medicao2H.java
│       │   │   ├── Pendencia2H.java
│       │   │   ├── Estrela.java
│       │   │   └── LogAlteracao.java
│       │   ├── excel/
│       │   │   ├── ExcelService.java         # Leitura/escrita .xlsx via Apache POI
│       │   │   └── ExcelWatcherService.java  # Polling 5min + WatchService NIO
│       │   ├── repository/                   # Interfaces JPA
│       │   ├── security/
│       │   │   ├── JwtUtil.java
│       │   │   ├── JwtAuthFilter.java
│       │   │   └── UserDetailsServiceImpl.java
│       │   └── service/
│       │       ├── DashboardService.java
│       │       ├── RefeicaoService.java
│       │       ├── Medicao2HService.java
│       │       └── EstrelasService.java
│       └── resources/
│           └── application.yml               # Perfis dev e prod
├── frontend/                         # HTML puro
│   ├── index.html                    # Login
│   ├── css/style.css                 # Tema noturno cosmos
│   ├── js/
│   │   ├── config.js                 # URL da API
│   │   └── api.js                    # Camada centralizada de API + UI helpers
│   └── pages/
│       ├── dashboard.html            # Cards métricas + pendências + histórico
│       ├── refeicoes.html            # CRUD refeições
│       ├── medicoes.html             # Registro medições 2H
│       ├── estrelas.html             # Contador de estrelas
│       └── logs.html                 # Auditoria (admin only)
├── docker-compose.yml
└── README.md
```

---

## Pré-requisitos

- Java 17+
- Maven 3.6+
- MySQL 8.0+
- Docker + Docker Compose (para deploy containerizado)

---

## Execução Local (Desenvolvimento)

### 1. Criar banco de dados MySQL

```sql
CREATE DATABASE glicemia_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. Configurar `application.yml`

O perfil `dev` está pré-configurado para `localhost:3306` com usuário `root` e senha `root`. Ajuste se necessário em `backend/src/main/resources/application.yml`.

### 3. Executar o backend

```bash
cd backend
mvn spring-boot:run
```

O servidor sobe em `http://localhost:8080`. Na primeira inicialização, o `DataInitializer` cria automaticamente os usuários Teo e Lui.

### 4. Abrir o frontend

Abra `frontend/index.html` com um servidor estático (ex: Live Server do VS Code) ou diretamente pelo arquivo.

> ⚠️ O CORS está configurado para `http://localhost:3000` e `http://127.0.0.1:5500`. Ajuste em `application.yml` se necessário.

---

## Execução com Docker

```bash
# Na raiz do projeto
docker-compose up --build
```

Isso inicializa o MySQL, aguarda o health check, e sobe o backend na porta 8080.

Para produção, configure as variáveis de ambiente no `docker-compose.yml`:
- `JWT_SECRET` — segredo forte para assinar os tokens
- `CORS_ALLOWED_ORIGINS` — URL do seu frontend em produção

---

## API REST

Todas as rotas (exceto `/api/login`) exigem o header:
```
Authorization: Bearer <token>
```

| Método | Rota | Acesso | Descrição |
|--------|------|--------|-----------|
| POST | `/api/login` | Público | Autenticação, retorna JWT |
| GET | `/api/dashboard` | USER/ADMIN | Resumo: glicemia, pendências, alertas |
| GET | `/api/refeicoes` | USER/ADMIN | Lista refeições |
| POST | `/api/refeicoes` | USER/ADMIN | Cria refeição (gera pendência 2H auto) |
| PUT | `/api/refeicoes/{id}` | USER/ADMIN | Atualiza refeição |
| DELETE | `/api/refeicoes/{id}` | ADMIN | Exclui refeição em cascata |
| POST | `/api/refeicoes/{id}/medicoes2h` | USER/ADMIN | Registra medição 2H |
| GET | `/api/pendencias` | USER/ADMIN | Lista pendências abertas |
| GET | `/api/estrelas` | USER/ADMIN | Quantidade de estrelas da Lui |
| POST | `/api/estrelas/add` | ADMIN | Adiciona estrelas |
| POST | `/api/estrelas/remove` | ADMIN | Remove estrelas |
| GET | `/api/logs` | ADMIN | Últimas 50 ações auditadas |

### Exemplo de Login

```bash
curl -X POST http://localhost:8080/api/login \
  -H "Content-Type: application/json" \
  -d '{"username":"Teo","password":"REMOVED_PASSWORD"}'
```

Resposta:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "role": "ROLE_ADMIN",
  "username": "Teo"
}
```

### Exemplo de Criar Refeição

```bash
curl -X POST http://localhost:8080/api/refeicoes \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "tipo": "ALMOCO",
    "medicaoAntesHora": "2026-05-21T12:00",
    "valorAntes": 110,
    "inicio": "2026-05-21T12:05",
    "fim": "2026-05-21T12:45",
    "observacao": "Almoço saudável"
  }'
```

---

## Integração Excel

O arquivo `dados.xlsx` é criado automaticamente na raiz do projeto (configurável via `app.excel.path`).

### Abas da planilha

| Aba | Colunas |
|-----|---------|
| **Refeições** | ID, Usuário, Tipo, MedAntes_DataHora, ValorAntes, Inicio_Ref, Fim_Ref, Observação |
| **Medições** | ID, RefeiçãoID, Horario_Medicao, Valor_Glicemia, Observação |
| **Pendências 2H** | ID, RefeiçãoID, Horario_Previsto, Status, Medicao2H_ID |
| **Logs** | ID, Timestamp, Usuário, Ação, Detalhes |

### Sincronização

- **Sistema → Excel:** A cada operação CRUD, o Excel é reescrito automaticamente.
- **Excel → Sistema:** Um job a cada 5 minutos detecta alterações via `lastModified`. Se o arquivo mudou externamente, importa os dados para o banco.

---

## Regras de Negócio

- Múltiplas refeições do mesmo tipo são permitidas no mesmo dia.
- Ao criar uma refeição, uma **pendência de medição 2H** é gerada automaticamente para 2 horas após o término.
- Valores de glicemia fora da faixa **50–150 mg/dL** geram alertas visuais (vermelho pulsante no dashboard).
- O administrador pode criar/editar registros em nome da Lui.
- Todas as ações são registradas na tabela `LOG_ALTERACAO`.
- As estrelas da Lui só podem ser modificadas pelo administrador.

---

## Banco de Dados (Modelo ER Simplificado)

```
USUARIO (1) ──── (N) REFEICAO (1) ──── (1) PENDENCIA_2H
                          │
                          └──── (N) MEDICAO_2H
                          └──── (N) LOG_ALTERACAO

USUARIO (1) ──── (1) ESTRELA
```

---

## Deploy em Produção (Render / Railway)

1. Crie um banco MySQL no serviço de sua escolha.
2. Defina as variáveis de ambiente:
   - `DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD`
   - `JWT_SECRET` (string longa e aleatória)
   - `CORS_ALLOWED_ORIGINS` (URL do frontend)
3. Atualize `frontend/js/config.js` com a URL do backend em produção.
4. Hospede o frontend no GitHub Pages ou Netlify.

---

## Telas do Sistema

| Tela | Descrição |
|------|-----------|
| **Login** | Tela inicial com animação de estrelas, autenticação JWT |
| **Dashboard** | Cards: última glicemia, pendências, taxa de medições, estrelas; alertas visuais; histórico |
| **Refeições** | Tabela com CRUD; modal para criar/editar; ações admin |
| **Medições 2H** | Lista pendências abertas; modal de registro; histórico de medições |
| **Estrelas da Lui** | Contador animado; admin pode +/- estrelas com input de quantidade |
| **Logs** | Tabela de auditoria com filtro por tipo de ação; stats resumidos (admin only) |

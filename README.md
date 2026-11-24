# DeliveryTech API

API REST de uma plataforma de delivery, desenvolvida como projeto final da disciplina de **Arquitetura de Sistemas** (FAT).

**Autor:** Rogério Sá de Macedo

## Funcionalidades

- Cadastro e autenticação de usuários com **JWT** (perfis `CLIENTE`, `RESTAURANTE`, `ADMIN` e `ENTREGADOR`)
- CRUD de clientes, restaurantes, produtos e pedidos
- Validação de dados e tratamento padronizado de erros (`GlobalExceptionHandler`)
- **Cache** de consultas de produtos, com invalidação automática nas alterações
- Documentação interativa com **Swagger/OpenAPI**
- Monitoramento com **Actuator**, métricas **Prometheus** e logs estruturados (`logback`)
- Testes automatizados com relatório de cobertura **JaCoCo**

## Tecnologias

Java 21 · Spring Boot 4.1 · Spring Web · Spring Data JPA · Spring Security · JJWT · ModelMapper · SpringDoc (Swagger) · H2 · Lombok · Maven · Docker

## Como executar

### Opção 1: Docker (recomendada)

Pré-requisito: Docker com o plugin Compose.

```bash
git clone https://github.com/Rogersamacedo/deliverytech.git
cd deliverytech
docker compose up --build
```

A primeira execução baixa as dependências e leva alguns minutos. A aplicação leva cerca de 1 minuto para subir. Quando o log mostrar `Started DeliveryApiApplication`, ela está pronta.

Para encerrar:

```bash
docker compose down
```

### Opção 2: Maven local

Pré-requisito: JDK 21.

```bash
export JWT_SECRET="troque-por-uma-chave-com-pelo-menos-32-caracteres"
./mvnw spring-boot:run
```

## Variáveis de ambiente

| Variável | Obrigatória | Padrão | Descrição |
|---|---|---|---|
| `JWT_SECRET` | Sim (fora do Docker) | chave de estudo no `docker-compose.yml` | Chave de assinatura do JWT. **Mínimo de 32 caracteres.** |
| `JWT_EXPIRATION` | Não | `86400000` | Validade do token em milissegundos (24 horas). |

No Docker, para usar outra chave, crie um arquivo `.env` ao lado do `docker-compose.yml`:

```
JWT_SECRET=sua-chave-com-pelo-menos-32-caracteres
```

## Endereços da aplicação

| Recurso | URL |
|---|---|
| Swagger UI | http://localhost:8080/swagger-ui.html |
| Demonstração de login JWT | http://localhost:8080/jwt-demo.html |
| Health | http://localhost:8080/health |
| Info | http://localhost:8080/info |
| Actuator (saúde) | http://localhost:8080/actuator/health |
| Console do H2 | http://localhost:8080/h2-console |

## Como usar a API

1. Registre um usuário: `POST /api/auth/register`
2. Faça login: `POST /api/auth/login`. A resposta traz o `token`.
3. Nas demais chamadas, envie o cabeçalho `Authorization: Bearer <token>`. No Swagger, use o botão **Authorize**.

Exemplo:

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"nome":"Joao Silva","email":"joao@email.com","senha":"123456","role":"CLIENTE"}'

curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"joao@email.com","senha":"123456"}'
```

Rotas públicas (sem token): `/api/auth/**`, `/api/restaurantes`, `/api/produtos`, `/health`, `/info`, Swagger e `/actuator/health`. As demais exigem autenticação. A lista completa de endpoints está no Swagger.

> O banco é o **H2 em memória**: os dados são apagados sempre que a aplicação reinicia. Registre o usuário de novo depois de subir.

## Cache

As consultas de produtos (por id, por categoria e por restaurante) são cacheadas em memória. Qualquer cadastro, alteração, exclusão ou mudança de disponibilidade invalida os caches de produtos.

| Método | Rota | Descrição |
|---|---|---|
| GET | `/api/cache` | Lista os caches existentes |
| DELETE | `/api/cache` | Limpa todos os caches |
| DELETE | `/api/cache/{nome}` | Limpa um cache específico |

## Testes

```bash
export JWT_SECRET="troque-por-uma-chave-com-pelo-menos-32-caracteres"
./mvnw clean test
```

O relatório de cobertura é gerado em `target/site/jacoco/index.html`.

## Estrutura do projeto

```
src/main/java/com/deliverytech/delivery_api/
├── config/        configurações (segurança, Swagger, ModelMapper, monitoramento)
├── controller/    endpoints REST
├── dto/           objetos de entrada (req) e saída (res)
├── enums/         enumerações do domínio
├── exeption/      tratamento global de erros
├── model/         entidades JPA
├── repository/    acesso a dados (Spring Data)
├── security/      JWT: geração, validação e filtro de autenticação
└── service/       regras de negócio (interfaces e implementações)
src/main/resources/  application.yml, application.properties, data.sql, logback-spring.xml
src/test/            testes de config, controller, security e service
```

## Problemas comuns

- **A aplicação não sobe e o log cita `JWT_SECRET`:** a variável não foi definida ou tem menos de 32 caracteres.
- **`curl` retorna "Empty reply" logo após subir:** a aplicação ainda está iniciando. Aguarde cerca de 1 minuto.
- **Login retorna "Credenciais inválidas":** o banco é apagado a cada reinício. Registre o usuário de novo.
- **Porta 8080 em uso:** altere o mapeamento de portas em `docker-compose.yml` (por exemplo, `"8081:8080"`).

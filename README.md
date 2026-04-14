# VectoPath Knowledge Api - Resource Manager with Vectorization

VectoPath Knowledge Api is an intelligent resource management API with vectorization and semantic search capabilities using PostgreSQL with the pgvector extension. The application integrates Spring Security with OAuth2 to secure resource access and provides fine-grained role management to control permissions.

## Architecture

Hexagonal architecture (Ports & Adapters) with clear separation of concerns:

- **Business**: Business domain, models and use cases
- **Client**: Primary adapters (REST API)
- **Infrastructure**: Secondary adapters (Database, configurations)

## Features

### Resource Management
- Creation and storage of textual resources
- Support for multiple source types:
  - **Direct text**: Creation from textual content
  - **URL**: Download and content extraction from a URL (with jsoup)
  - **File**: Import of text files
- Automatic content vectorization
- Processing status tracking (PENDING, PROCESSING, VECTORIZED, ERROR)
- Search by name and filtering by status
- Custom metadata management (JSON)
- Traceability with source fields (source_type, source_url, source_content_type)

### Semantic Search
- Intelligent content chunking
- Vectorization with OpenAI Embeddings (text-embedding-3-small, 1536 dimensions)
- Cosine similarity search in pgvector with HNSW index
- Semantic search API with configurable limit
- Chunk retrieval by resource

### Security
- OAuth2 authentication with JWT
- Role and permission management at resource level
- Customizable CORS configuration
- MDC for user traceability in logs

## Technologies Used

- **Framework**: Spring Boot 3.5.6
- **Language**: Java 21
- **Database**: PostgreSQL with pgvector extension
- **AI/ML**: Spring AI 1.0.0, OpenAI Embeddings
- **Security**: Spring Security, OAuth2 Resource Server
- **Testing**: JUnit 5, Testcontainers, ArchUnit
- **Scraping**: jsoup 1.21.2

## Project Structure

```
src/main/java/com/laulem/vectopath/
├── business/           # Business layer (domain)
│   ├── model/         # Domain models
│   ├── repository/    # Ports (interfaces)
│   ├── service/       # Business services (use cases) / Ports (interfaces)
│   └── exception/     # Business exceptions
├── client/            # Client layer (primary adapters)
│   ├── controller/    # REST controllers
│   ├── dto/          # DTOs for REST API
│   └── service/      # Orchestrators
├── infra/            # Infrastructure layer (secondary adapters)
│   ├── conf/         # Configurations (Security, CORS, MDC)
│   ├── entity/       # JPA entities
│   ├── repository/   # Repository implementations
│   ├── service/      # Technical services
│   └── properties/   # Externalized properties
└── shared/           # Shared code
```

## Quick Start

### Prerequisites
- Java 21
- Maven 3.x
- PostgreSQL with pgvector extension
- OpenAI API Key
- OAuth2 Server (production only)

### Getting Started

#### 1. PostgreSQL Database with pgvector

Start PostgreSQL with Docker Compose (includes pgvector extension):
```bash
cd infra/container
docker-compose up -d
```

The database will be accessible at:
- Host: `localhost:5432`
- Database: `vecto_path`
- User: `user`
- Password: `password`

To verify the container is running:
```bash
docker ps | grep vecto-path-pgvector-db
```

To stop the database:
```bash
docker-compose down
```

#### 2. Environment Variables Configuration

Minimum configuration (required):
```bash
export OPENAI_API_KEY=sk-your-api-key-here
```

#### 3. Starting the Application

```bash
# Local mode (without OAuth2 security)
./mvnw spring-boot:run -Dspring-boot.run.profiles=local

# Production mode (with OAuth2 security)
./mvnw spring-boot:run
```

The API is available at `http://localhost:8080`

## API Endpoints

### Resources

#### Create a resource (direct text)
```http
POST /api/v1/resources
Content-Type: application/json

{
  "name": "Example document",
  "content": "Here is the content of my document...",
  "content_type": "text/plain",
  "source_type": "TEXT",
  "metadata": "{\"source\":\"upload\",\"author\":\"user\"}",
  "access_level": "PUBLIC",
  "allowed_roles": ["ROLE_USER"]
}
```

#### Create a resource from a URL
```http
POST /api/v1/resources
Content-Type: application/json

{
  "name": "Wikipedia Article",
  "source_type": "URL",
  "source_url": "https://en.wikipedia.org/wiki/Artificial_intelligence",
  "access_level": "PUBLIC"
}
```

#### Upload a file
```http
POST /api/v1/resources/upload
Content-Type: multipart/form-data

file: [file]
name: "My document"
metadata: "{"category":"documentation"}"
access_level: "PRIVATE"
allowed_roles: ["ROLE_ADMIN"]
```

#### List all resources
```http
GET /api/v1/resources
```

#### Retrieve a resource
```http
GET /api/v1/resources/{id}
```

#### Search resources by name
```http
GET /api/v1/resources/search?name=example
```

#### Filter by status
```http
GET /api/v1/resources/status/VECTORIZED
```

Available statuses:
- `PENDING`: Waiting for processing
- `PROCESSING`: Being vectorized
- `VECTORIZED`: Successfully vectorized
- `ERROR`: Error during processing
- `DELETED`: Deleted

#### Reprocess
```http
POST /api/v1/resources/{id}/reprocess
```

#### Delete a resource
```http
DELETE /api/v1/resources/{id}
```

### Semantic Search

#### Semantic search
```http
POST /api/v1/search/semantic
Content-Type: application/json

{
  "query": "How does vectorization work?",
  "limit": 10
}
```

## Configuration

### Environment Variables

#### OpenAI (Required)
- `OPENAI_API_KEY`: OpenAI API key for embeddings **(REQUIRED)**
- `OPENAI_EMBEDDING_MODEL`: Embedding model (default: `text-embedding-3-small`)
- `OPENAI_EMBEDDING_OPTIONS_MODEL`: Embedding model options (default: `text-embedding-3-small`)

#### Database
- `DATABASE_URL`: PostgreSQL database URL (default: `jdbc:postgresql://localhost:5432/vecto_path`)
- `DATABASE_USERNAME`: PostgreSQL user (default: `user`)
- `DATABASE_PASSWORD`: PostgreSQL password (default: `password`)
- `DATABASE_DRIVER`: JDBC driver (default: `org.postgresql.Driver`)

#### JPA/Hibernate
- `JPA_DDL_AUTO`: DDL mode (default: `none`)
- `JPA_SHOW_SQL`: Show SQL queries (default: `false`)
- `HIBERNATE_FORMAT_SQL`: Format SQL queries (default: `false`)
- `JPA_OPEN_IN_VIEW`: Open Session In View (default: `false`)

#### OAuth2 Security
- `JWT_ISSUER_URI`: OAuth2 server URI (default: `http://localhost:9000`)
- `SECURITY_ADMIN_ROLE`: Administrator role (default: `admin`)
- `SECURITY_NOT_AFFECTABLE_ROLES`: Non-assignable roles (default: `admin,search.semantic`)
- `SECURITY_SCOPE_SEARCH_SEMANTIC`: Scope for semantic search (default: `search.semantic`)
- `SECURITY_SCOPE_RESOURCES_READ`: Scope for reading resources (default: `resources.read`)
- `SECURITY_SCOPE_RESOURCES_WRITE`: Scope for writing resources (default: `resources.write`)
- `SECURITY_SCOPE_RESOURCES_DELETE`: Scope for deleting resources (default: `resources.delete`)

#### PGVector
- `PGVECTOR_INDEX_TYPE`: Index type (default: `HNSW`) - possible values: `HNSW`, `IVFFLAT`
- `PGVECTOR_DISTANCE_TYPE`: Distance type (default: `COSINE_DISTANCE`)
- `PGVECTOR_DIMENSIONS`: Vector dimensions (default: `1536`)
- `PGVECTOR_MAX_DOCUMENT_BATCH_SIZE`: Maximum batch size (default: `10000`)

#### CORS
- `CORS_ALLOWED_ORIGINS`: Allowed origins (default: `http://localhost:3000,http://localhost:4200,http://localhost:8080,http://localhost:9000`)
- `CORS_ALLOWED_METHODS`: Allowed HTTP methods (default: `GET,POST,PUT,PATCH,DELETE,OPTIONS`)
- `CORS_ALLOWED_HEADERS`: Allowed headers (default: `Authorization,Content-Type,X-Requested-With,Accept,Origin,Access-Control-Request-Method,Access-Control-Request-Headers`)
- `CORS_EXPOSED_HEADERS`: Headers exposed to the client (default: `Access-Control-Allow-Origin,Access-Control-Allow-Credentials`)
- `CORS_ALLOW_CREDENTIALS`: Allow credentials (default: `false`)
- `CORS_MAX_AGE`: Preflight cache duration in seconds (default: `3600`)

#### Content
- `CONTENT_DOWNLOAD_TIMEOUT`: Download timeout in seconds (default: `30`)
- `CONTENT_DOWNLOAD_CONNECT_TIMEOUT`: Connection timeout in seconds (default: `10`)

#### Application
- `APPLICATION_TITLE`: Application title (default: `VectoPath`)
- `APPLICATION_VERSION`: Application version (default: pom.xml version)
- `SERVER_PORT`: Server port (default: `8080`)

#### Logging
- `LOGGING_LEVEL_VECTOPATH`: Log level for VectoPath (default: `INFO`)
- `LOGGING_LEVEL_SPRING_AI`: Log level for Spring AI (default: `INFO`)
- `LOGGING_PATTERN_CONSOLE`: Console log pattern (default: `%d{yyyy-MM-dd HH:mm:ss} - %msg%n`)
- `JACKSON_TIME_ZONE`: Jackson timezone (default: `UTC`)

### Configuration Examples

#### Minimal Configuration (Local)
```bash
export OPENAI_API_KEY=sk-your-api-key-here
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

#### Production Configuration
```bash
# OpenAI
export OPENAI_API_KEY=sk-your-api-key-here

# OAuth2
export JWT_ISSUER_URI=https://auth.myapp.com

# Database
export DATABASE_URL=jdbc:postgresql://db.myapp.com:5432/vectopath_prod
export DATABASE_USERNAME=vectopath_user
export DATABASE_PASSWORD=secure_password

# CORS
export CORS_ALLOWED_ORIGINS=https://myapp.com,https://www.myapp.com
export CORS_ALLOW_CREDENTIALS=true

./mvnw spring-boot:run
```

#### application.yml File (Example)
```yaml
spring:
  ai:
    openai:
      api-key: ${OPENAI_API_KEY}
      embedding:
        model: ${OPENAI_EMBEDDING_MODEL:text-embedding-3-small}
        options:
          model: ${OPENAI_EMBEDDING_OPTIONS_MODEL:text-embedding-3-small}
    vectorstore:
      pgvector:
        index-type: ${PGVECTOR_INDEX_TYPE:HNSW}
        distance-type: ${PGVECTOR_DISTANCE_TYPE:COSINE_DISTANCE}
        dimensions: ${PGVECTOR_DIMENSIONS:1536}
        max-document-batch-size: ${PGVECTOR_MAX_DOCUMENT_BATCH_SIZE:10000}

  datasource:
    url: ${DATABASE_URL:jdbc:postgresql://localhost:5432/vecto_path}
    username: ${DATABASE_USERNAME:user}
    password: ${DATABASE_PASSWORD:password}
    driver-class-name: ${DATABASE_DRIVER:org.postgresql.Driver}

  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: ${JWT_ISSUER_URI:http://localhost:9000}

security:
  admin-role: ${SECURITY_ADMIN_ROLE:admin}
  scopes:
    search:
      semantic: ${SECURITY_SCOPE_SEARCH_SEMANTIC:search.semantic}
    resources:
      read: ${SECURITY_SCOPE_RESOURCES_READ:resources.read}
      write: ${SECURITY_SCOPE_RESOURCES_WRITE:resources.write}
      delete: ${SECURITY_SCOPE_RESOURCES_DELETE:resources.delete}
  cors:
    allowed-origins: ${CORS_ALLOWED_ORIGINS:http://localhost:3000,http://localhost:4200}
    allowed-methods: ${CORS_ALLOWED_METHODS:GET,POST,PUT,PATCH,DELETE,OPTIONS}
    allowed-headers: ${CORS_ALLOWED_HEADERS:Authorization,Content-Type,X-Requested-With,Accept,Origin}
    exposed-headers: ${CORS_EXPOSED_HEADERS:Access-Control-Allow-Origin,Access-Control-Allow-Credentials}
    allow-credentials: ${CORS_ALLOW_CREDENTIALS:false}
    max-age: ${CORS_MAX_AGE:3600}

content:
  download:
    timeout-seconds: ${CONTENT_DOWNLOAD_TIMEOUT:30}
    connect-timeout-seconds: ${CONTENT_DOWNLOAD_CONNECT_TIMEOUT:10}

server:
  port: ${SERVER_PORT:8080}
```

### Customization

- **Chunk size**: Modify `DEFAULT_CHUNK_SIZE` in `ResourceServiceImpl`
- **Embedding model**: Change `OPENAI_EMBEDDING_MODEL` or `spring.ai.openai.embedding.model` (default: text-embedding-3-small)
- **Search limit**: Adjust the `limit` parameter in API requests
- **pgvector index type**: HNSW (recommended) or IVFFLAT via `PGVECTOR_INDEX_TYPE` or `spring.ai.vectorstore.pgvector.index-type`
- **Vector dimensions**: 1536 dimensions for OpenAI via `PGVECTOR_DIMENSIONS` or `spring.ai.vectorstore.pgvector.dimensions`

## Security

### Authentication and Authorization

VectoPath uses Spring Security with OAuth2 Resource Server (JWT) to secure resource access.

#### Role Management

The role system allows controlling access to resources:
- `app_roles` table: Stores available roles
- `resource_allowed_roles` table: Associates resources with authorized roles
- Authenticated users must have the appropriate role to access a resource

#### Protected Endpoints
- `/actuator/health`, `/actuator/info`: Public access
- `/api/v1/**`: Authentication required
- Other endpoints are protected by default

### Security Profiles
- **Production**: OAuth2 security enabled (default profile)
- **Local/Test**: Security disabled (`local` and `test` profiles)

## Development

### Testing
```bash
./mvnw test
```

Tests include:
- Integration tests with Testcontainers
- Architectural tests with ArchUnit (hexagonal architecture validation)
- REST controller tests

### Build
```bash
./mvnw clean package
```

### Docker
```bash
docker build -t vectopath .
docker run -p 8080:8080 vectopath
```

## TODO
- [ ] Paginated data retrieval
- [ ] Allow database authentication (in addition to OAuth2)
- [ ] Test distribution as a library to be extended
- [ ] Provide Swagger/OpenAPI and remove business layer dependency in DTOs
- [ ] Rename app_roles (more explicit name)
- [ ] Create CRUD operations for app_roles
- [ ] Extend supported file types for vectorization
- [ ] Provide a sample Bruno collection
- [ ] Add other embeddings (e.g., HuggingFace)
- [ ] Add more integration & unit tests

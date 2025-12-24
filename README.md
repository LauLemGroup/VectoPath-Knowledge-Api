# VectoPath - Gestionnaire de Ressources avec Vectorisation

VectoPath est une API de gestion intelligente de ressources avec capacités de vectorisation et de recherche sémantique utilisant PostgreSQL avec l'extension pgvector.

## Architecture

L'application suit une architecture hexagonale avec les couches suivantes :

- **Business** : Modèles de domaine et services métier
- **Client** : Contrôleurs REST et DTOs
- **Infrastructure** : Repositories PostgreSQL et configurations

## Fonctionnalités

### Gestion des Ressources
- Création et stockage de ressources textuelles
- Vectorisation automatique du contenu
- Suivi du statut de traitement (PENDING, PROCESSING, VECTORIZED, ERROR)
- Recherche par nom et filtrage par statut

### Recherche Sémantique
- Découpage intelligent du contenu en chunks
- Vectorisation avec OpenAI Embeddings (text-embedding-ada-002)
- Recherche par similarité cosinus dans pgvector
- API de recherche sémantique

## Démarrage

### Prérequis
- Java 21
- PostgreSQL avec extension pgvector
- Clé API OpenAI

### Configuration
1. Démarrez PostgreSQL avec Docker Compose :
```bash
cd infra/container
docker-compose up -d
```

2. Configurez votre clé OpenAI (optionnel) :
```bash
export OPENAI_API_KEY=your_api_key_here
```

3. Démarrez l'application :
```bash
./mvnw spring-boot:run
```

L'API est disponible sur `http://localhost:8080`

## API Endpoints

### Ressources

#### Créer une ressource
```http
POST /api/v1/resources
Content-Type: application/json

{
  "name": "Document exemple",
  "content": "Voici le contenu de mon document...",
  "content_type": "text/plain",
  "metadata": "{\"source\":\"upload\",\"author\":\"user\"}"
}
```

#### Lister toutes les ressources
```http
GET /api/v1/resources
```

#### Récupérer une ressource
```http
GET /api/v1/resources/{id}
```

#### Rechercher des ressources par nom
```http
GET /api/v1/resources/search?name=exemple
```

#### Filtrer par statut
```http
GET /api/v1/resources/status/VECTORIZED
```

#### Relancer le traitement
```http
POST /api/v1/resources/{id}/reprocess
```

#### Supprimer une ressource
```http
DELETE /api/v1/resources/{id}
```

### Recherche Sémantique

#### Recherche sémantique
```http
POST /api/v1/search/semantic
Content-Type: application/json

{
  "query": "Comment fonctionne la vectorisation ?",
  "limit": 10
}
```

#### Récupérer les chunks d'une ressource
```http
GET /api/v1/search/chunks/resource/{resourceId}
```

#### Récupérer un chunk spécifique
```http
GET /api/v1/search/chunks/{id}
```

## Statuts des Ressources

- `PENDING` : En attente de traitement
- `PROCESSING` : En cours de vectorisation
- `VECTORIZED` : Vectorisée avec succès
- `ERROR` : Erreur lors du traitement
- `DELETED` : Supprimée

## Structure de Base de Données

### Table `resources`
Stocke les métadonnées des ressources :
- `id` : UUID unique
- `name` : Nom de la ressource
- `content` : Contenu textuel complet
- `content_type` : Type de contenu
- `status` : Statut de traitement
- `metadata` : Métadonnées JSON
- `created_at`, `updated_at` : Timestamps

### Table `doc_chunk_1536`
Stocke les chunks vectorisés :
- `id` : UUID unique
- `resource_id` : Référence vers la ressource
- `content` : Contenu du chunk
- `metadata` : Métadonnées JSON du chunk
- `embedding` : Vecteur 1536 dimensions (OpenAI)
- `created_at` : Timestamp

## Exemples d'Usage

### 1. Ajouter un document et le vectoriser
```bash
curl -X POST http://localhost:8080/api/v1/resources \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Guide utilisateur",
    "content": "Ce guide explique comment utiliser VectoPath pour gérer vos documents et effectuer des recherches sémantiques...",
    "content_type": "text/plain",
    "metadata": "{\"category\":\"documentation\",\"language\":\"fr\"}"
  }'
```

### 2. Rechercher dans les documents
```bash
curl -X POST http://localhost:8080/api/v1/search/semantic \
  -H "Content-Type: application/json" \
  -d '{
    "query": "comment utiliser la recherche sémantique",
    "limit": 5
  }'
```

### 3. Vérifier le statut d'une ressource
```bash
curl http://localhost:8080/api/v1/resources/{resource-id}
```

## Configuration Avancée

### Variables d'Environnement
- `OPENAI_API_KEY` : Clé API OpenAI pour les embeddings
- `SPRING_DATASOURCE_URL` : URL de la base PostgreSQL
- `SPRING_DATASOURCE_USERNAME` : Utilisateur PostgreSQL
- `SPRING_DATASOURCE_PASSWORD` : Mot de passe PostgreSQL

### Configuration CORS

La configuration CORS est externalisée dans les fichiers `application.yml` et peut être personnalisée pour chaque environnement.

#### Variables d'environnement CORS disponibles :
- `CORS_ALLOWED_ORIGINS` : Origines autorisées (séparées par des virgules)
- `CORS_ALLOWED_METHODS` : Méthodes HTTP autorisées
- `CORS_ALLOWED_HEADERS` : En-têtes autorisés
- `CORS_EXPOSED_HEADERS` : En-têtes exposés au client
- `CORS_ALLOW_CREDENTIALS` : Autoriser les credentials (true/false)
- `CORS_MAX_AGE` : Durée de cache de la pré-vérification (en secondes)

#### Exemple de configuration pour production :
```bash
export CORS_ALLOWED_ORIGINS=https://monapp.com,https://www.monapp.com
export CORS_ALLOW_CREDENTIALS=true
```

#### Configuration dans application.yml :
```yaml
security:
  cors:
    allowed-origins: http://localhost:3000,http://localhost:4200
    allowed-methods: GET,POST,PUT,PATCH,DELETE,OPTIONS
    allowed-headers: Authorization,Content-Type,X-Requested-With,Accept,Origin
    exposed-headers: Access-Control-Allow-Origin,Access-Control-Allow-Credentials
    allow-credentials: false
    max-age: 3600
```

### Personnalisation
- Taille des chunks : Modifiez `DEFAULT_CHUNK_SIZE` dans `ResourceServiceImpl`
- Modèle d'embedding : Changez `spring.ai.openai.embedding.model`
- Limite de recherche : Ajustez dans les requêtes API

## Développement

### Tests
```bash
./mvnw test
```

### Construction
```bash
./mvnw clean package
```

### Docker
```bash
docker build -t vectopath .
docker run -p 8080:8080 vectopath
```
# TODO
- Ajouter une gestion de sécurité (authentification, autorisation)
- Implémenter des fonctionnalités de logging et monitoring avancées

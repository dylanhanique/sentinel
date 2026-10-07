# Modèle de données

Chaque service possède sa base. Les schémas sont versionnés avec **Flyway** (`src/main/resources/db/migration`).

## api_db

```mermaid
erDiagram
    USER ||--o{ MONITOR : possede
    MONITOR ||--o{ CHECK_RESULT : produit
    MONITOR ||--o{ INCIDENT : historise

    USER {
        uuid id PK
        string email UK
        string password_hash
        timestamp created_at
    }
    MONITOR {
        uuid id PK
        uuid user_id FK
        string name
        string url
        int interval_seconds
        int expected_status_code
        int timeout_ms
        int failure_threshold
        boolean active
        int version "incrémenté à chaque modification"
        string status_copy "UP | DOWN | UNKNOWN"
        timestamp created_at
        timestamp updated_at
    }
    CHECK_RESULT {
        uuid check_id PK "idempotence"
        uuid monitor_id FK
        timestamp checked_at
        boolean success
        int status_code
        int response_time_ms
        string error
    }
    INCIDENT {
        uuid id PK "= incidentId de l'événement"
        uuid monitor_id FK
        timestamp started_at
        timestamp resolved_at "null si en cours"
        string cause
    }
```

La table `outbox_event` est technique et omise du diagramme :

| Colonne | Type | Rôle |
|---|---|---|
| `id` | uuid | Identifiant de l'événement |
| `routing_key` | text | Ex. `monitor.upserted` |
| `payload` | jsonb | Enveloppe complète |
| `created_at` | timestamp | Ordre de publication |
| `published_at` | timestamp | Nul tant que non publié |

## checker_db

```mermaid
erDiagram
    MONITOR_TO_CHECK {
        uuid monitor_id PK
        int version
        string url
        int interval_seconds
        int expected_status_code
        int timeout_ms
        boolean active
        timestamp next_check_at "index"
    }
```

## alert_db

```mermaid
erDiagram
    MONITOR_STATE ||--o{ INCIDENT : ouvre
    MONITOR_STATE {
        uuid monitor_id PK
        int version
        string name
        string owner_email
        int failure_threshold
        string status "UP | DOWN | UNKNOWN"
        int consecutive_failures
    }
    INCIDENT {
        uuid id PK
        uuid monitor_id FK
        timestamp started_at
        timestamp resolved_at
        string cause
    }
    PROCESSED_CHECK {
        uuid check_id PK
        timestamp processed_at
    }
```

## Points de conception

- **`CHECK_RESULT` est la table qui grossit** : 1 moniteur à 1 min = 1 440 lignes par jour. Index sur `(monitor_id, checked_at)`, purge à 30 jours. Évolution possible : partitionnement par date.
- **Un seul incident ouvert par moniteur** : index unique partiel `CREATE UNIQUE INDEX ... ON incident (monitor_id) WHERE resolved_at IS NULL`.
- **Idempotence** : `check_id` en clé primaire (api_db) et table `processed_check` (alert_db) permettent d'ignorer les doublons.
- **Versions** : `version` est comparée à l'arrivée d'un `MonitorUpserted` ; un événement de version inférieure ou égale est ignoré.
- **Index de planification (checker)** : `CREATE INDEX ON monitor_to_check (next_check_at) WHERE active`.
- **Purge d'`outbox_event`** : suppression des lignes publiées depuis plus de 7 jours.
- **Suppression de compte** : `ON DELETE CASCADE` sur `monitor`, `check_result` et `incident`, et un `MonitorDeleted` publié par moniteur.

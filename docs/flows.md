# Flux principaux

## 1. Création d'un moniteur

```mermaid
sequenceDiagram
    participant U as Utilisateur
    participant A as api-service
    participant DB as api_db
    participant R as Relais Outbox
    participant Q as RabbitMQ
    participant C as checker-service
    participant L as alert-service

    U->>A: POST /api/monitors
    A->>DB: INSERT monitor + INSERT outbox_event (même transaction)
    A-->>U: 201 Created
    R->>DB: Lit les événements non publiés
    R->>Q: MonitorUpserted
    Q-->>R: Confirm (publisher confirm)
    R->>DB: Marque comme publié
    Q->>C: MonitorUpserted
    C->>C: Upsert monitor_to_check
    Q->>L: MonitorUpserted
    L->>L: Upsert monitor_state
```

## 2. Vérification et alerte

```mermaid
sequenceDiagram
    participant C as checker-service
    participant W as Site surveillé
    participant Q as RabbitMQ
    participant A as api-service
    participant L as alert-service
    participant U as Utilisateur

    C->>C: SELECT ... FOR UPDATE SKIP LOCKED
    C->>W: Requête HTTP
    W-->>C: Réponse ou timeout
    C->>Q: CheckCompleted
    Q->>A: CheckCompleted
    A->>A: Enregistre le résultat (idempotent sur checkId)
    Q->>L: CheckCompleted
    L->>L: Incrémente ou remet à zéro les échecs
    alt Seuil atteint
        L->>L: Ouvre l'incident, statut DOWN
        L->>Q: MonitorStatusChanged
        Q->>A: MonitorStatusChanged
        A->>A: Met à jour status_copy, enregistre l'incident
        L->>U: Email de panne
    end
```

## 3. Machine à états d'un moniteur (alert-service)

```mermaid
stateDiagram-v2
    [*] --> UNKNOWN
    UNKNOWN --> UP: 1 succès
    UNKNOWN --> DOWN: N échecs consécutifs
    UP --> DOWN: N échecs consécutifs
    DOWN --> UP: 1 succès
    UP --> UP: succès (compteur à 0)
    UP --> UP: échec < N (compteur +1)
```

Un moniteur en pause (`active = false`) ne produit plus de vérifications ; son état est conservé.

## 4. Scheduling sans doublons (checker-service)

Chaque instance exécute périodiquement :

```sql
SELECT * FROM monitor_to_check
WHERE active AND next_check_at <= now()
ORDER BY next_check_at
LIMIT 50
FOR UPDATE SKIP LOCKED;
```

Les lignes verrouillées par une instance sont ignorées par les autres : chaque instance traite un lot différent. Après vérification, `next_check_at` est repoussé de `interval_seconds`.

## 5. Démarrage à froid du checker

```mermaid
sequenceDiagram
    participant C as checker-service (nouveau)
    participant A as api-service
    C->>A: GET /internal/monitors
    A-->>C: Liste complète des moniteurs
    C->>C: Upsert de chaque moniteur (en respectant version)
    C->>C: Démarre la consommation des événements
```

Appelé uniquement quand `checker_db` est vide. L'endpoint n'est pas exposé par l'Ingress.

## 6. Suppression de compte

```mermaid
sequenceDiagram
    participant U as Utilisateur
    participant A as api-service
    participant DB as api_db
    participant Q as RabbitMQ
    U->>A: DELETE /api/users/me
    A->>DB: DELETE user (cascade) + INSERT outbox MonitorDeleted x N
    A-->>U: 204 No Content
    Note over A,Q: Le relais publie un MonitorDeleted par moniteur
    Q->>Q: checker et alert suppriment leurs copies
```

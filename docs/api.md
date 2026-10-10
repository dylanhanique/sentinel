# API REST de l'api-service

Base : `/api`. Authentification par `Authorization: Bearer <JWT>` sauf `register` et `login`. Documentation interactive générée par springdoc-openapi (`/swagger-ui.html`).

## Compte

| Méthode | Route | Description | Réponses |
|---|---|---|---|
| POST | `/api/auth/register` | Créer un compte `{email, password}` | 201, 400, 409 (email déjà pris) |
| POST | `/api/auth/login` | Se connecter | 200 `{accessToken, expiresIn}`, 401 |
| GET | `/api/accounts/me` | Profil de l'utilisateur connecté | 200 |
| DELETE | `/api/accounts/me` | Supprimer son compte (cascade) | 204 |

## Moniteurs

| Méthode | Route | Description | Réponses |
|---|---|---|---|
| POST | `/api/monitors` | Créer | 201 + `Location`, 400 |
| GET | `/api/monitors` | Lister les siens (paginé : `page`, `size`) | 200 |
| GET | `/api/monitors/{id}` | Détail | 200, 404 |
| PUT | `/api/monitors/{id}` | Modifier | 200, 400, 404 |
| DELETE | `/api/monitors/{id}` | Supprimer | 204, 404 |
| POST | `/api/monitors/{id}/pause` | Mettre en pause | 200, 404 |
| POST | `/api/monitors/{id}/resume` | Réactiver | 200, 404 |

## Métriques

| Méthode | Route | Description |
|---|---|---|
| GET | `/api/monitors/{id}/stats?period=24h` | Disponibilité, temps moyen, p95. `period` : `24h`, `7d`, `30d` |
| GET | `/api/monitors/{id}/results?from=&to=&page=&size=` | Historique des vérifications |
| GET | `/api/monitors/{id}/incidents` | Historique des incidents |

## Interne (jamais exposé par l'Ingress)

| Méthode | Route | Description |
|---|---|---|
| GET | `/internal/monitors` | Tous les moniteurs, pour la resynchronisation du checker au démarrage |

Protégé par une NetworkPolicy Kubernetes et un token partagé (secret).

## Exemples

### Inscription

```json
// POST /api/auth/register
{ "email": "alice@example.com", "password": "S3cret-passw0rd" }
```

### Connexion

```json
// POST /api/auth/login  →  200
{ "accessToken": "eyJhbGciOi...", "expiresIn": 3600 }
```

### Création d'un moniteur

```json
// POST /api/monitors
{
  "name": "Mon blog",
  "url": "https://blog.example.com",
  "intervalSeconds": 60,
  "expectedStatusCode": 200,
  "timeoutMs": 5000,
  "failureThreshold": 3
}
```

```json
// 201 Created
{
  "id": "c1f0a9d2-5b7e-4c3a-8f21-0e9d8c7b6a55",
  "name": "Mon blog",
  "url": "https://blog.example.com",
  "intervalSeconds": 60,
  "expectedStatusCode": 200,
  "timeoutMs": 5000,
  "failureThreshold": 3,
  "active": true,
  "status": "UNKNOWN",
  "createdAt": "2026-10-07T09:30:00Z"
}
```

### Statistiques

```json
// GET /api/monitors/{id}/stats?period=24h
{
  "period": "24h",
  "uptimePercent": 99.72,
  "avgResponseTimeMs": 184,
  "p95ResponseTimeMs": 390,
  "totalChecks": 1440,
  "failedChecks": 4
}
```

### Erreur (RFC 7807)

```json
// 400
{
  "type": "about:blank",
  "title": "Validation failed",
  "status": 400,
  "detail": "intervalSeconds must be greater than or equal to 60",
  "errors": [{ "field": "intervalSeconds", "message": "must be >= 60" }]
}
```

## Conventions

- **Validation** : voir [use-cases.md](use-cases.md) (contraintes des champs).
- **Contrôle d'accès** : une ressource d'un autre utilisateur renvoie **404**, jamais 403.
- **Erreurs** : format `application/problem+json` (`ProblemDetail` de Spring Boot 4.1.1).
- **Mot de passe** : minimum 10 caractères, haché avec BCrypt, jamais renvoyé ni journalisé.
- **JWT** : signé (HS256 avec secret externalisé, ou RS256), durée de vie courte.
- **CORS** : limité à l'origine du front.
- **Dates** : ISO 8601 UTC. **Pagination** : `page` (à partir de 0) et `size` (max 100).
- **Effet sur les événements** : toute création, modification, pause, reprise ou suppression écrit un événement dans l'Outbox dans la même transaction (voir [events.md](events.md)).

# Contrats des événements

Source de vérité des messages échangés via RabbitMQ. Voir [messaging.md](messaging.md) pour la topologie.

## Enveloppe commune

```json
{
  "eventId": "uuid, unique par message",
  "eventType": "MonitorUpserted",
  "occurredAt": "2026-10-07T09:30:00Z",
  "payload": { }
}
```

Dates en **ISO 8601 UTC**. Identifiants en **UUID**.

## MonitorUpserted

- **Routing key** : `monitor.upserted`
- **Producteur** : api-service (via Outbox)
- **Consommateurs** : checker-service, alert-service
- **Émis quand** : création, modification, pause, reprise d'un moniteur

```json
{
  "eventId": "7d1c5e0a-3b1e-4a56-9d0a-1a2b3c4d5e6f",
  "eventType": "MonitorUpserted",
  "occurredAt": "2026-10-07T09:30:00Z",
  "payload": {
    "monitorId": "c1f0a9d2-5b7e-4c3a-8f21-0e9d8c7b6a55",
    "version": 3,
    "ownerEmail": "alice@example.com",
    "name": "Mon blog",
    "url": "https://blog.example.com",
    "intervalSeconds": 60,
    "expectedStatusCode": 200,
    "timeoutMs": 5000,
    "failureThreshold": 3,
    "active": true
  }
}
```

**Traitement par le consommateur**

1. Si `version` ≤ version connue pour ce `monitorId` : ignorer.
2. Sinon : upsert de l'état complet.
3. Checker : si nouveau moniteur, `next_check_at = now()`. Si `intervalSeconds` change, recalculer `next_check_at`.

## MonitorDeleted

- **Routing key** : `monitor.deleted`
- **Producteur** : api-service (via Outbox)
- **Consommateurs** : checker-service, alert-service
- **Émis quand** : suppression d'un moniteur ou du compte propriétaire

```json
{
  "eventId": "2b9a6f14-0c7d-4f3e-b6a1-5d4c3b2a1f00",
  "eventType": "MonitorDeleted",
  "occurredAt": "2026-10-07T10:02:00Z",
  "payload": {
    "monitorId": "c1f0a9d2-5b7e-4c3a-8f21-0e9d8c7b6a55",
    "version": 4
  }
}
```

**Traitement** : supprimer la copie locale. Si le moniteur est déjà inconnu, ne rien faire.

## CheckCompleted

- **Routing key** : `check.completed`
- **Producteur** : checker-service
- **Consommateurs** : api-service, alert-service
- **Émis quand** : à chaque vérification terminée (succès, échec ou timeout)

```json
{
  "eventId": "e5f4d3c2-b1a0-4987-8654-321098765432",
  "eventType": "CheckCompleted",
  "occurredAt": "2026-10-07T09:31:00Z",
  "payload": {
    "checkId": "a3b4c5d6-1111-2222-3333-444455556666",
    "monitorId": "c1f0a9d2-5b7e-4c3a-8f21-0e9d8c7b6a55",
    "checkedAt": "2026-10-07T09:31:00Z",
    "success": false,
    "statusCode": 503,
    "responseTimeMs": 412,
    "error": "UNEXPECTED_STATUS"
  }
}
```

| Champ | Précision |
|---|---|
| `statusCode` | Nul en cas de timeout ou d'erreur réseau |
| `error` | Nul en cas de succès, sinon : `TIMEOUT`, `DNS_FAILURE`, `CONNECTION_REFUSED`, `UNEXPECTED_STATUS`, `SSL_ERROR`, `BLOCKED_TARGET` |

**Traitement**

- **api-service** : insérer dans `check_result` avec `ON CONFLICT (check_id) DO NOTHING`. Ignorer si le moniteur est inconnu.
- **alert-service** : dans une transaction, insérer `checkId` dans `processed_check` (si déjà présent : ignorer), puis mettre à jour le compteur d'échecs et l'état.

## MonitorStatusChanged

- **Routing key** : `monitor.status-changed`
- **Producteur** : alert-service (via Outbox)
- **Consommateur** : api-service
- **Émis quand** : passage UP/DOWN/UNKNOWN d'un moniteur

```json
{
  "eventId": "11112222-3333-4444-5555-666677778888",
  "eventType": "MonitorStatusChanged",
  "occurredAt": "2026-10-07T09:33:00Z",
  "payload": {
    "monitorId": "c1f0a9d2-5b7e-4c3a-8f21-0e9d8c7b6a55",
    "incidentId": "9f8e7d6c-aaaa-bbbb-cccc-ddddeeeeffff",
    "oldStatus": "UP",
    "newStatus": "DOWN",
    "cause": "3 échecs consécutifs (dernier : UNEXPECTED_STATUS 503)"
  }
}
```

Le passage `DOWN → UP` réutilise le même `incidentId`.

**Traitement (api-service)** : mettre à jour `monitor.status_copy` ; si `newStatus = DOWN`, créer l'incident (`id = incidentId`, `started_at = occurredAt`) ; si `oldStatus = DOWN`, renseigner `resolved_at` de l'incident correspondant.

## Règles d'évolution des contrats

- On **n'enlève ni ne renomme** jamais un champ : on en **ajoute** (compatibilité ascendante).
- Les consommateurs **ignorent les champs inconnus** (`FAIL_ON_UNKNOWN_PROPERTIES = false`).
- Chaque service teste ses (dé)sérialisations sur des fichiers JSON d'exemple.
- Tout changement de contrat est consigné ici avant d'être codé.

## Limites connues

- Un `MonitorUpserted` en retard peut « ressusciter » un moniteur supprimé chez un consommateur. Toléré au MVP (les résultats d'un moniteur inconnu sont ignorés). Correctif : conserver une pierre tombale (tombstone) après suppression.
- Aucune garantie d'ordre globale entre événements de types différents.

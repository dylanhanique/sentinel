# ADR 0003 : Pattern Outbox pour la publication d'événements

**Statut** : Accepté

## Contexte
Un service qui enregistre une donnée puis publie un événement fait deux opérations sur deux systèmes (PostgreSQL et RabbitMQ). Un crash entre les deux laisse les systèmes incohérents. Une transaction distribuée (XA) est trop lourde.

## Décision
Écrire l'événement dans la table `outbox_event` **dans la même transaction** que la donnée métier. Un relais planifié lit les événements non publiés (`published_at IS NULL`), les publie avec confirmation (publisher confirms), puis renseigne `published_at`.

Appliqué à l'api-service et à l'alert-service. Le checker publie directement (avec confirms) : la perte d'un résultat de vérification est tolérable.

## Conséquences
- (+) Aucun événement perdu : si la donnée est enregistrée, l'événement partira.
- (+) Simple, sans coordinateur de transaction.
- (−) Livraison « au moins une fois » : des doublons sont possibles, les consommateurs doivent être idempotents.
- (−) Latence ajoutée (intervalle du relais, quelques secondes au plus).
- (−) Plusieurs instances du relais doivent se coordonner (`FOR UPDATE SKIP LOCKED`).
- Purge périodique des événements publiés.

## Alternatives écartées
- **Publier après le commit sans outbox** : perte possible d'événements.
- **Change Data Capture (Debezium)** : élégant, mais une brique d'infrastructure de plus.
- **Transaction distribuée (2PC / XA)** : complexe et peu supporté par les brokers modernes.

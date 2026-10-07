# ADR 0002 : RabbitMQ plutôt que Kafka

**Statut** : Accepté

## Contexte
Les services échangent des événements de faible à moyen volume (quelques milliers de vérifications par minute au plus). Le besoin : routage souple, retries, dead-letter queues, plusieurs consommateurs indépendants.

## Décision
Utiliser **RabbitMQ** avec un exchange `topic` et une queue par consommateur.

## Conséquences
- (+) Mise en place simple, interface de gestion pratique, DLQ et retries natifs.
- (+) Modèle « queue par service » adapté à notre cas.
- (+) Plus léger à exploiter que Kafka pour ce volume.
- (−) Pas de rejeu de l'historique des messages après consommation.
- (−) Moins adapté à un très fort débit ou au traitement de flux.

## Alternatives écartées
- **Kafka** : excellent pour le très gros volume, le rejeu et le streaming, mais surdimensionné et plus lourd à opérer ici. Pourrait être étudié en évolution.
- **Appels HTTP synchrones** : couplage temporel fort, gestion des pannes à reconstruire.

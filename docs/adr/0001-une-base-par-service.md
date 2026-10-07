# ADR 0001 : Une base de données par service

**Statut** : Accepté

## Contexte
Trois services (api, checker, alert) doivent partager des informations sur les moniteurs. Une base commune est plus simple, mais couple les services par le schéma.

## Décision
Chaque service possède sa base (`api_db`, `checker_db`, `alert_db`) et son utilisateur PostgreSQL. Personne ne lit la base d'un autre service. Les données partagées voyagent par événements. Les trois bases sont hébergées sur **un seul serveur** PostgreSQL (local) ou **une seule instance** Cloud SQL (GCP) pour limiter les coûts.

## Conséquences
- (+) Services réellement indépendants, schéma modifiable sans coordination.
- (+) Isolation vérifiable : un service ne peut physiquement pas lire les données d'un autre.
- (−) Données dupliquées (copies locales des moniteurs), cohérence éventuelle.
- (−) Pas de jointure entre services, pas de transaction globale.
- (−) Nécessite des mécanismes fiables de propagation (voir ADR 0003 et 0004).

## Alternatives écartées
- **Base partagée** : plus simple, mais microservices uniquement de façade.
- **Un schéma par service dans une base** : compromis possible, mais moins d'isolation.

# ADR 0005 : Planification des vérifications avec `FOR UPDATE SKIP LOCKED`

**Statut** : Accepté

## Contexte
Plusieurs instances du checker doivent se répartir les vérifications à lancer, sans en exécuter une deux fois ni en oublier.

## Décision
Le checker possède une table `monitor_to_check` avec une colonne `next_check_at`. Chaque instance sélectionne un lot d'échéances avec `FOR UPDATE SKIP LOCKED`, exécute les vérifications, puis repousse `next_check_at`.

## Conséquences
- (+) Pas de verrou distribué externe (Redis, ShedLock), pas de coordinateur.
- (+) Scalabilité horizontale directe, compatible avec un HPA.
- (+) Une instance qui plante relâche ses verrous : le travail est repris.
- (−) Couplage à PostgreSQL pour la planification.
- (−) Charge de lecture/écriture régulière sur `checker_db` (index partiel sur `next_check_at`).
- Les vérifications dont l'exécution dépasse la durée de la transaction demandent un schéma en deux temps (réservation avec un « lease » puis exécution).

## Alternatives écartées
- **ShedLock / verrou Redis** : un composant de plus, granularité grossière.
- **Planificateur central** (Quartz clusterisé) : plus lourd.
- **Un message RabbitMQ différé par vérification** : fragile et difficile à observer.

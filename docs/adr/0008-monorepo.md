# ADR 0008 : Monorepo

**Statut** : Accepté

## Contexte
Trois services, un front, des manifestes de déploiement et la documentation évoluent ensemble et sont maintenus par une seule personne.

## Décision
Un seul dépôt Git avec `services/`, `frontend/`, `deploy/`, `docs/`. La CI est conditionnée par chemin (build d'un service uniquement si son dossier change).

## Conséquences
- (+) Un seul endroit à cloner, commits atomiques entre services et contrats, vue d'ensemble pour un recruteur.
- (+) Contrats d'événements et documentation versionnés avec le code.
- (−) Pipelines à filtrer par chemin pour éviter de tout reconstruire.
- (−) Pas de versionnement indépendant des services (acceptable ici).

## Alternatives écartées
- **Un dépôt par service** : plus proche d'équipes multiples, mais surcoût de gestion pour un projet individuel.

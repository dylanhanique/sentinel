# Méthode de travail

Ce document décrit comment le projet est organisé. Il s'applique à tous les tickets.

## Rythme

- **Sprints d'une semaine**, quatre sprints.
- **Planning** (lundi) : choix des tickets à partir de l'objectif du sprint.
- **Daily** (chaque jour, 5 minutes) : un commentaire dans le ticket en cours : fait, en cours, bloquant.
- **Review et rétrospective** (vendredi) : ce qui est fini, ce qui ne l'est pas, pourquoi, ce que je change.

## Tableau

GitHub Projects, colonnes : `Backlog` → `Ready` → `In progress` → `In review` → `Done`.

Chaque ticket est une GitHub Issue dont le **titre est préfixé par son identifiant** : `SEN-6 : Sécuriser /api/** avec le JWT`. Le numéro attribué par GitHub n'est pas l'identifiant du ticket. Labels : `feature`, `tech`, `docs`, `bug`.

## Format d'un ticket

```
Titre                  : verbe d'action, orienté résultat
Contexte               : pourquoi ce ticket existe
Critères d'acceptation : liste vérifiable
Pistes techniques      : indices, pas la solution
Hors périmètre         : ce qu'on ne fait pas ici
```

**Estimation** : S = ½ journée, M = 1 journée, L = 2 jours. Un ticket plus gros que L se découpe.

## Workflow Git

- `main` est toujours stable : aucun push direct.
- Une branche par ticket : `feature/SEN-2-database-flyway`.
- Commits au format **Conventional Commits** : `feat(api): add account registration (SEN-4)`. Types : `feat`, `fix`, `docs`, `test`, `chore`, `refactor`.
- Une Pull Request par ticket, relue par son auteur avant de demander une review.
- Fusion en **Squash and merge** : un commit sur `main` par ticket. Le titre de la PR devient le message du commit, sans point final.
- Branche supprimée après le merge.

## Dette technique

Une dette volontaire est toujours marquée dans le code par `TODO(SEN-x)` pointant vers un ticket **existant**, et mentionnée dans la section « Points d'attention » de la PR.

## Definition of Done

Un ticket est terminé uniquement si **toutes** les conditions suivantes sont remplies :

1. Les critères d'acceptation du ticket sont vérifiés.
2. Ils sont couverts par des tests automatisés (un test que je n'ai jamais vu échouer ne compte pas).
3. La CI est verte *(applicable à partir de SEN-3)*.
4. La documentation concernée dans `docs/` est à jour : `api.md`, `events.md`, `data-model.md`, un ADR si une décision d'architecture a été prise.
5. Aucun secret n'est versionné.
6. Les dettes volontaires sont marquées `TODO(SEN-x)` et listées dans la PR.
7. La PR est relue, mergée en squash, et le ticket déplacé en `Done`.

## Description de PR

Sections : **Contexte** (ticket, `Closes #n`), **Ce qui change**, **Décisions**, **Comment vérifier**, **Points d'attention**. Le modèle est pré-rempli par `.github/pull_request_template.md`.

## ADR

Toute décision structurante donne lieu à un ADR court dans `docs/adr/` : contexte, décision, conséquences (avec la justification des inconvénients acceptés), alternatives écartées. Chaque ADR est ajouté à l'index `docs/adr/README.md`.

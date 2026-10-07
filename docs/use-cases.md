# Cas d'usage et règles métier

## Cas d'usage (MVP)

### Compte

| ID | Cas d'usage |
|---|---|
| UC1 | Un utilisateur peut créer un compte (email unique, mot de passe haché avec BCrypt) |
| UC2 | Un utilisateur peut se connecter et obtenir un JWT |
| UC3 | Un utilisateur peut supprimer son compte : ses moniteurs, résultats et incidents sont supprimés en cascade |

### Moniteurs

| ID | Cas d'usage |
|---|---|
| UC4 | Un utilisateur peut créer un moniteur |
| UC5 | Un utilisateur peut lister ses moniteurs et consulter le détail de l'un d'eux |
| UC6 | Un utilisateur peut modifier un moniteur |
| UC7 | Un utilisateur peut supprimer un moniteur |
| UC8 | Un utilisateur peut mettre un moniteur en pause et le réactiver |

### Métriques

| ID | Cas d'usage |
|---|---|
| UC9 | Un utilisateur peut consulter la disponibilité (%), le temps de réponse moyen et le p95 sur 24 h, 7 j ou 30 j |
| UC10 | Un utilisateur peut consulter l'historique des vérifications d'un moniteur |
| UC11 | Un utilisateur peut consulter l'historique des incidents d'un moniteur |

### Alertes (système)

| ID | Cas d'usage |
|---|---|
| UC12 | Le système détecte qu'un site est tombé et envoie un email au propriétaire |
| UC13 | Le système envoie un email quand le site est rétabli |

## Règles métier

| ID | Règle |
|---|---|
| R1 | Un moniteur passe DOWN après **N échecs consécutifs** (3 par défaut, configurable de 1 à 10) |
| R2 | Un email n'est envoyé que lors d'un **changement d'état** |
| R3 | Un utilisateur ne voit et ne modifie que **ses propres** moniteurs. Pour une ressource d'un autre utilisateur, l'API répond 404 |
| R4 | Intervalle de vérification minimum : **60 secondes** |
| R5 | Les résultats sont conservés **30 jours**, puis purgés par un CronJob |
| R6 | Un seul incident ouvert par moniteur à la fois |
| R7 | Un moniteur en pause n'est pas vérifié et n'envoie aucune alerte |
| R8 | Le checker refuse de vérifier les adresses privées ou internes (protection SSRF) |
| R9 | Seules les URL `http` et `https` sont acceptées |

## Validation des champs d'un moniteur

| Champ | Contrainte |
|---|---|
| `name` | 1 à 100 caractères |
| `url` | http/https, 2048 caractères max |
| `intervalSeconds` | ≥ 60 |
| `expectedStatusCode` | 100 à 599 |
| `timeoutMs` | 1 000 à 30 000 |
| `failureThreshold` | 1 à 10 |

## Hors MVP (bonus)

Page de statut publique, webhook Slack/Discord, changement de mot de passe, vérification de certificat SSL, vérifications TCP, multi-régions, équipes et organisations.

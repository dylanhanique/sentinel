# Documentation de Sentinel

Plateforme de monitoring de sites web : architecture événementielle (Spring Boot, RabbitMQ, PostgreSQL), front Angular, déploiement Docker / Kubernetes / Helm / Terraform.

## Index

| Document | Contenu |
|---|---|
| [architecture.md](architecture.md) | Vision, périmètre, services, stack, structure du repo |
| [use-cases.md](use-cases.md) | Cas d'usage et règles métier |
| [data-model.md](data-model.md) | Modèles de données (diagrammes ER) |
| [flows.md](flows.md) | Diagrammes de séquence des flux principaux |
| [messaging.md](messaging.md) | RabbitMQ : concepts et topologie du projet |
| [events.md](events.md) | Contrats des événements (payloads JSON) |
| [api.md](api.md) | Endpoints REST de l'api-service |
| [wireframes.md](wireframes.md) | Maquettes des écrans Angular |
| [pitfalls.md](pitfalls.md) | Pièges à anticiper |
| [roadmap.md](roadmap.md) | Planning sur 4 semaines et suivi d'avancement |
| [adr/](adr/) | Architecture Decision Records (choix justifiés) |

## Conventions

- Les diagrammes sont en **Mermaid** (rendus nativement par GitHub).
- Les contrats d'événements et l'API sont la **source de vérité** : toute modification passe d'abord par ces documents.
- Chaque décision structurante donne lieu à un ADR court.

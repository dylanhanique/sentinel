# Architecture Decision Records

Un ADR documente **une** décision structurante : contexte, décision, conséquences, alternatives écartées. Statuts : *Proposé*, *Accepté*, *Remplacé*.

| ADR | Décision | Statut |
|---|---|---|
| [0001](0001-une-base-par-service.md) | Une base de données par service | Accepté |
| [0002](0002-rabbitmq-plutot-que-kafka.md) | RabbitMQ plutôt que Kafka | Accepté |
| [0003](0003-pattern-outbox.md) | Pattern Outbox pour la publication d'événements | Accepté |
| [0004](0004-evenements-etat-complet-versionnes.md) | Événements à état complet, versionnés | Accepté |
| [0005](0005-scheduling-skip-locked.md) | Planification des vérifications avec `SKIP LOCKED` | Accepté |
| [0006](0006-authentification-jwt.md) | Authentification JWT gérée par l'api-service | Accepté |
| [0007](0007-terraform-plutot-qu-ansible.md) | Terraform pour l'infrastructure, Ansible en bonus | Accepté |
| [0008](0008-monorepo.md) | Monorepo | Accepté |
| [0009](0009-maven-plutot-que-gradle.md) | Maven | Accepté |

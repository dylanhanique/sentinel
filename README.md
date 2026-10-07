# Sentinel

Plateforme de monitoring de sites web : architecture événementielle (Spring Boot, RabbitMQ, PostgreSQL), front Angular, déploiement Docker / Kubernetes / Helm / Terraform.

> Projet en construction. La documentation de conception est dans [`docs/`](docs/README.md).

## Démarrage rapide (infrastructure locale)

Prérequis : Docker et Docker Compose.

```bash
make up          # ou : docker compose -f deploy/docker-compose.yml up -d
```

| Service | URL / port | Identifiants (dev) |
|---|---|---|
| PostgreSQL | `localhost:5432` | voir `deploy/docker-compose.yml` |
| RabbitMQ (AMQP) | `localhost:5672` | `sentinel` / `sentinel` |
| RabbitMQ (interface web) | http://localhost:15672 | `sentinel` / `sentinel` |
| Mailpit (interface web) | http://localhost:8025 | aucun |
| Mailpit (SMTP) | `localhost:1025` | aucun |

Bases PostgreSQL : `api_db` (`api_user`), `checker_db` (`checker_user`), `alert_db` (`alert_user`).

## Structure

```
services/   api-service, checker-service, alert-service
frontend/   Angular
deploy/     docker-compose, Helm, Terraform
docs/       conception, contrats, ADR
```

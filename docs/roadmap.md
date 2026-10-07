# Roadmap (4 semaines)

Règle d'or : **tout doit fonctionner en local avant de toucher au cloud.**

## Semaine 1 : fondations et api-service

- [ ] Jours 1-2 : documentation (`docs/`), monorepo, `docker-compose.yml` (PostgreSQL avec trois bases, RabbitMQ, Mailpit)
- [ ] Jours 3-5 : api-service
  - [ ] Flyway et schéma `api_db`
  - [ ] Inscription, connexion JWT, suppression de compte
  - [ ] CRUD moniteurs avec contrôle d'accès
  - [ ] Outbox + relais de publication
  - [ ] Tests d'intégration (Testcontainers)

## Semaine 2 : checker et alert

- [ ] checker-service
  - [ ] Consommation de `MonitorUpserted` / `MonitorDeleted`
  - [ ] Scheduling `FOR UPDATE SKIP LOCKED`
  - [ ] Exécution HTTP et protection SSRF
  - [ ] Publication de `CheckCompleted`
  - [ ] Resynchronisation au démarrage
- [ ] alert-service
  - [ ] Machine à états et incidents
  - [ ] Idempotence (`processed_check`)
  - [ ] Emails (Mailpit en local)
  - [ ] Outbox pour `MonitorStatusChanged`
- [ ] api-service : consommation de `CheckCompleted` et `MonitorStatusChanged`, endpoints de métriques
- [ ] Test de bout en bout : créer un moniteur, couper un site, recevoir l'email

## Semaine 3 : Angular, Docker, CI

- [ ] Jours 1-2 : apprentissage Angular (composants, services HTTP, routing, formulaires réactifs, guards, interceptors)
- [ ] Jours 3-5 : écrans (login, liste, formulaire, détail avec graphiques)
- [ ] En parallèle : Dockerfiles multi-stage, pipeline GitHub Actions (build, tests, image, scan Trivy)

## Semaine 4 : Kubernetes, cloud, finition

- [ ] Manifests Kubernetes puis chart Helm, déploiement sur kind
- [ ] HPA du checker, CronJob de purge, NetworkPolicy
- [ ] Terraform + GKE + Cloud SQL, déploiement automatisé
- [ ] Prometheus et Grafana (dashboard des services)
- [ ] Test de charge k6
- [ ] README final, schéma d'architecture, GIF de démo
- [ ] Bonus : AWS SES, Ansible, page de statut publique

## Définition de « terminé » pour chaque service

- Tests unitaires et d'intégration verts
- Dockerfile qui build
- Endpoints Actuator (`/actuator/health`, `/actuator/prometheus`) actifs
- Logs structurés
- Documentation à jour dans `docs/`

## Journal d'avancement

| Date | Fait | Reste / blocages |
|---|---|---|
| | | |

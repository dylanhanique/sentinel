# Pièges à anticiper

## 1. Double écriture (le plus important)

Enregistrer en base puis publier dans RabbitMQ n'est pas atomique : un crash entre les deux fait diverger les services.

**Solution : pattern Outbox.** L'événement est écrit dans `outbox_event` dans la même transaction que la donnée métier. Un relais planifié lit les événements non publiés, les envoie à RabbitMQ, attend la confirmation, puis les marque publiés. Obligatoire pour l'api-service et l'alert-service. Pour le checker, une publication directe avec publisher confirms suffit : perdre un résultat de vérification est tolérable.

## 2. Livraison « au moins une fois »

Les doublons arrivent (crash du relais entre l'envoi et le marquage, ou d'un consumer avant l'ack). Tous les consumers doivent être **idempotents** : upsert complet pour les états, `checkId` unique pour `CheckCompleted`.

## 3. Ordre des messages

Aucune garantie globale. `MonitorUpserted` porte un numéro de `version` ; un événement de version inférieure ou égale à l'état connu est ignoré.

## 4. Messages empoisonnés

Un message invalide retenté à l'infini bloque la queue. Prévoir retries avec backoff, puis dead-letter queue, et une procédure pour inspecter et rejouer les messages de la DLQ.

## 5. Démarrage à froid

Un nouveau checker a une base vide. Compromis du MVP : endpoint interne `GET /internal/monitors`, appelé au démarrage si la base est vide, non exposé par l'Ingress. À documenter comme compromis assumé.

## 6. Cohérence éventuelle

Un moniteur créé n'est connu du checker qu'une fraction de seconde plus tard. Acceptable, à expliquer dans le README.

## 7. Sécurité

- **Contrôle d'accès par propriétaire** : tests dédiés (un utilisateur ne doit jamais pouvoir lire, modifier ou supprimer le moniteur d'un autre).
- **SSRF** : un utilisateur ne doit pas pouvoir faire vérifier `http://localhost`, `169.254.169.254` (métadonnées cloud) ou les IP internes du cluster. Le checker résout le DNS lui-même, vérifie l'IP obtenue contre les plages privées, et se connecte à cette IP (pour éviter le DNS rebinding). Les redirections sont soit désactivées, soit revalidées.
- **Secrets** hors du repo (variables d'environnement, Kubernetes Secrets, Secret Manager).
- **Mots de passe** en BCrypt, jamais journalisés.
- **Limitation de débit** sur login et register.

## 8. Cascade et RGPD

La suppression de compte doit supprimer toutes les données personnelles, y compris dans `checker_db` et `alert_db` (email du propriétaire). C'est assuré par les `MonitorDeleted`. À tester de bout en bout.

## 9. Croissance de `check_result`

Index sur `(monitor_id, checked_at)`, purge à 30 jours par CronJob, calcul des statistiques par requêtes d'agrégation sur des plages bornées. Évolution : partitionnement par date.

## 10. Dérive des coûts cloud

Terraform pour tout détruire d'une commande, alertes de budget GCP, ne jamais laisser le cluster tourner pour rien. Le cloud vient en fin de projet.

## 11. Trop d'ambition

La roadmap est serrée. En cas de retard, couper dans l'ordre : AWS, Ansible, page de statut, multi-environnements. Ne jamais couper les tests, la CI ni le README.

## 12. Tests d'intégration trop lents ou instables

Utiliser Testcontainers avec réutilisation des conteneurs, des attentes explicites (Awaitility) plutôt que des `sleep`, et séparer tests unitaires et tests d'intégration dans la CI.

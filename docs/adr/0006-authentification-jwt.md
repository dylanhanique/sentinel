# ADR 0006 : Authentification JWT gérée par l'api-service

**Statut** : Accepté

## Contexte
Une seule application front interagit avec un seul service exposé (api-service). Le besoin d'authentification est simple : inscription, connexion, accès aux ressources de l'utilisateur.

## Décision
L'api-service gère les comptes (BCrypt) et émet des **JWT** de courte durée, vérifiés à chaque requête par Spring Security. Les secrets de signature sont externalisés.

## Conséquences
- (+) Simple, sans composant externe, stateless.
- (+) Aucune dépendance à un serveur d'identité pour la démonstration.
- (−) Pas de SSO, de MFA ou de gestion fine des rôles.
- (−) Révocation difficile avant expiration (durée de vie courte, refresh token éventuel).

## Alternatives écartées
- **Keycloak / OAuth2 / OIDC** : standard en entreprise et plus complet, mais lourd pour ce périmètre. Piste d'évolution documentée.
- **Sessions côté serveur** : demandent un état partagé entre instances.

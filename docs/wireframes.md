# Maquettes des écrans (Angular)

Schémas volontairement simples. À refaire éventuellement dans Figma ou draw.io.

## 1. Connexion / inscription

```
+--------------------------------------+
|              SENTINEL                |
|                                      |
|   Email     [______________________] |
|   Mot de passe [___________________] |
|                                      |
|   [        Se connecter           ]  |
|                                      |
|   Pas de compte ? Créer un compte    |
+--------------------------------------+
```

Route : `/login`, `/register`. Erreurs affichées sous les champs.

## 2. Liste des moniteurs (page d'accueil)

```
+--------------------------------------------------------------+
| SENTINEL                         alice@example.com [Déconnexion] |
+--------------------------------------------------------------+
| Mes moniteurs                              [ + Nouveau moniteur ] |
|                                                              |
| Statut  Nom          URL                    Dispo 24h  Latence |
|  (UP)   Mon blog     https://blog.exam...    99.7 %    184 ms  |
|  (DOWN) API paiement https://pay.exampl...   87.2 %    --      |
|  (II)   Site test    https://test.exam...    pause             |
|                                                              |
|                                   < 1 2 3 >                  |
+--------------------------------------------------------------+
```

Route : `/monitors`. Pastille de couleur selon le statut (UP vert, DOWN rouge, pause gris, UNKNOWN orange).

## 3. Création / modification d'un moniteur

```
+--------------------------------------+
| Nouveau moniteur                     |
|                                      |
| Nom                 [______________] |
| URL                 [https://_____ ] |
| Intervalle (s)      [60__________]   |
| Code HTTP attendu   [200_________]   |
| Timeout (ms)        [5000________]   |
| Échecs avant alerte [3___________]   |
|                                      |
|        [Annuler]   [Enregistrer]     |
+--------------------------------------+
```

Routes : `/monitors/new`, `/monitors/:id/edit`. Formulaire réactif avec validation identique à l'API.

## 4. Détail d'un moniteur

```
+--------------------------------------------------------------+
| < Retour    Mon blog (UP)     [Pause] [Modifier] [Supprimer] |
| https://blog.example.com                                     |
+--------------------------------------------------------------+
| Période : [24h] [7j] [30j]                                   |
|                                                              |
|  Disponibilité   Temps moyen   p95        Vérifications       |
|    99.72 %         184 ms      390 ms     1440 (4 échecs)     |
|                                                              |
|  Temps de réponse                                            |
|  ms ^                      .                                 |
|     |     .  .  ..   .  .'  '.   .                           |
|     +--------------------------------------> temps           |
|                                                              |
|  Incidents                                                   |
|  07/10 03:12 -> 03:19   3 échecs consécutifs (503)           |
|  02/10 21:40 -> 21:41   TIMEOUT                              |
|                                                              |
|  Dernières vérifications                                     |
|  09:31  OK    200   184 ms                                   |
|  09:30  ECHEC 503   412 ms                                   |
+--------------------------------------------------------------+
```

Route : `/monitors/:id`. Composants : cartes de stats, graphique, tableau d'incidents, tableau paginé des vérifications.

## Composants Angular à prévoir

| Composant / service | Rôle |
|---|---|
| `AuthService` | Login, stockage du token, déconnexion |
| `authInterceptor` | Ajoute le JWT aux requêtes, gère les 401 |
| `authGuard` | Protège les routes privées |
| `MonitorService` | Appels HTTP vers `/api/monitors` |
| `LoginComponent`, `RegisterComponent` | Écrans d'authentification |
| `MonitorListComponent` | Liste paginée |
| `MonitorFormComponent` | Création et modification |
| `MonitorDetailComponent` | Détail, stats, graphique |
| `StatusBadgeComponent` | Pastille de statut réutilisable |

## Hors MVP

Page de statut publique (`/status/:slug`) sans authentification, avec la liste des moniteurs choisis et leur état.

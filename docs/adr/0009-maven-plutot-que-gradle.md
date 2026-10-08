# ADR 0009 : Maven plutôt que Gradle

**Statut** : Accepté

## Contexte
Le projet regroupe trois services indépendants dans un monorepo. Il n'y a qu'un seul développeur et la deadline de livraison est la contrainte principale.

## Décision
Utiliser **Maven** comme outil de build, via le wrapper **mvnw** qui permet d'utiliser la même version de Maven sur tous les environnements sans installation préalable.

## Conséquences
- (+) Maven est généré par défaut par Spring Initializr, sa convention évite d'écrire de la configuration de build.
- (−) Gradle peut être plus rapide que Maven selon la taille du projet et l'usage du cache de build. De plus, il utilise l'incrémentalité pour ne pas recompiler ce qui n'a pas changé.
- (+) La différence de performances en secondes coûte moins cher que la courbe d'apprentissage de Gradle qui se mesure en jours.

## Alternatives écartées
- **Gradle** : offre plus de flexibilité et de possibilités d'optimisation, mais le coût d'apprentissage est disproportionné pour un projet de trois petits services construits par un seul développeur.

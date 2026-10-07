# ADR 0004 : Événements à état complet, versionnés

**Statut** : Accepté

## Contexte
Les services consommateurs doivent maintenir une copie locale des moniteurs. On peut émettre des événements de changement (créé, champ modifié, supprimé) ou l'état complet.

## Décision
`MonitorUpserted` transporte l'**état complet** du moniteur (y compris l'email du propriétaire : *event-carried state transfer*) et un numéro de **`version`** incrémenté à chaque modification. Le consommateur ignore un événement dont la version est inférieure ou égale à celle qu'il connaît, sinon il fait un upsert.

## Conséquences
- (+) Idempotence naturelle : rejouer ou dupliquer un événement est inoffensif.
- (+) Tolérance au désordre grâce à la version.
- (+) Le consommateur n'a jamais besoin d'appeler le producteur.
- (−) Messages plus volumineux.
- (−) Donnée personnelle (email) dupliquée : à couvrir par la suppression en cascade.
- (−) Un événement tardif peut ressusciter un moniteur supprimé : toléré au MVP, correctif par tombstone.

## Alternatives écartées
- **Événements delta** (`MonitorNameChanged`...) : plus fins, mais sensibles à l'ordre et à la perte.
- **Appel HTTP à l'api-service à chaque besoin** : couplage et dépendance à la disponibilité.

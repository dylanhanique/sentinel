# ADR 0007 : Terraform pour l'infrastructure, Ansible en bonus

**Statut** : Accepté

## Contexte
Le projet doit provisionner de l'infrastructure cloud (cluster Kubernetes managé, base de données, réseau) de façon reproductible et destructible pour maîtriser les coûts.

## Décision
Utiliser **Terraform** pour GKE, Cloud SQL et le réseau. **Helm** déploie l'application sur le cluster. Ansible n'est introduit qu'en bonus (par exemple pour configurer une machine ou automatiser un poste), car son rôle (configuration de serveurs) recoupe peu une infra managée.

## Conséquences
- (+) Infrastructure déclarative, versionnée, détruite d'une commande (`terraform destroy`).
- (+) Séparation nette : Terraform crée l'infra, Helm déploie l'application.
- (−) Gestion de l'état Terraform (backend distant à prévoir).

## Alternatives écartées
- **Ansible pour le provisionnement cloud** : possible, mais moins adapté à la gestion de cycle de vie des ressources.
- **Console cloud manuelle** : non reproductible.

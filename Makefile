COMPOSE = docker compose -f deploy/docker-compose.yml

.PHONY: up down reset logs ps psql-api

up:        ## Démarre PostgreSQL, RabbitMQ et Mailpit
	$(COMPOSE) up -d

down:      ## Arrête les conteneurs (données conservées)
	$(COMPOSE) down

reset:     ## Arrête et SUPPRIME toutes les données
	$(COMPOSE) down -v

logs:      ## Suit les logs
	$(COMPOSE) logs -f

ps:        ## État des conteneurs
	$(COMPOSE) ps

psql-api:  ## Ouvre psql sur api_db
	$(COMPOSE) exec postgres psql -U api_user -d api_db

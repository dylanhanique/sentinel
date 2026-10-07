#!/bin/bash
# Exécuté UNE SEULE FOIS, au premier démarrage du conteneur (volume vide).
# Crée une base et un utilisateur par service. Chaque utilisateur n'a accès qu'à sa base.
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    CREATE USER api_user     WITH PASSWORD '${API_DB_PASSWORD}';
    CREATE USER checker_user WITH PASSWORD '${CHECKER_DB_PASSWORD}';
    CREATE USER alert_user   WITH PASSWORD '${ALERT_DB_PASSWORD}';

    CREATE DATABASE api_db     OWNER api_user;
    CREATE DATABASE checker_db OWNER checker_user;
    CREATE DATABASE alert_db   OWNER alert_user;

    -- Par défaut, tout le monde peut se connecter à une base. On l'interdit.
    REVOKE CONNECT ON DATABASE api_db     FROM PUBLIC;
    REVOKE CONNECT ON DATABASE checker_db FROM PUBLIC;
    REVOKE CONNECT ON DATABASE alert_db   FROM PUBLIC;
EOSQL

CREATE EXTENSION IF NOT EXISTS citext;

CREATE TABLE account (
    id UUID DEFAULT gen_random_uuid(),
    email CITEXT NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT pk_account PRIMARY KEY (id),
    CONSTRAINT uk_account_email UNIQUE (email)
);

CREATE TABLE licenses (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    content_id TEXT NOT NULL,
    issued_at TIMESTAMP NOT NULL DEFAULT now()
);

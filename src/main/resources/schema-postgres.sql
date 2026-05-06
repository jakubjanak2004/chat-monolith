CREATE EXTENSION IF NOT EXISTS pg_trgm;

CREATE INDEX IF NOT EXISTS idx_chat_user_name_norm_trgm
    ON chat_user USING gin (name_normalized gin_trgm_ops);

CREATE INDEX IF NOT EXISTS idx_chat_name_norm_trgm
    ON chat USING gin (name_normalized gin_trgm_ops);

CREATE INDEX IF NOT EXISTS idx_message_content_trgm
    ON message USING gin (content gin_trgm_ops);

CREATE INDEX IF NOT EXISTS idx_message_chat_created
    ON message (chat_id, created DESC);

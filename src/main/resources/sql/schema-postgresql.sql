CREATE TABLE IF NOT EXISTS t_spring_ai_chat_memory
(
    id              BIGSERIAL    PRIMARY KEY,
    chatId          VARCHAR(36) NOT NULL,
    content         TEXT         NOT NULL,
    type            VARCHAR(50)  NOT NULL,
    timestamp       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_spring_ai_chat_memory_conversation_id ON t_spring_ai_chat_memory (chatId);
CREATE INDEX IF NOT EXISTS idx_spring_ai_chat_memory_timestamp ON t_spring_ai_chat_memory (timestamp);


CREATE TABLE IF NOT EXISTS spring_ai_chat_memory
(
    id              BIGSERIAL    PRIMARY KEY,
    conversation_id  VARCHAR(255) NOT NULL,
    content         TEXT         NOT NULL,
    type            VARCHAR(50)  NOT NULL,
    timestamp       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_spring_ai_chat_memory_conversation_id ON spring_ai_chat_memory (conversation_id);
CREATE INDEX IF NOT EXISTS idx_spring_ai_chat_memory_timestamp ON spring_ai_chat_memory (timestamp);
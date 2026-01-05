CREATE TABLE IF NOT EXISTS SPRING_AI_CHAT_MEMORY
(
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    conversation_id VARCHAR(255) NOT NULL,
    content         TEXT         NOT NULL,
    type            VARCHAR(50)  NOT NULL,
    `timestamp`     DATETIME     NOT NULL,
    INDEX idx_spring_ai_chat_memory_cid (conversation_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;
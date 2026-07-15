CREATE TABLE IF NOT EXISTS testimony (
    session_id VARCHAR(255) PRIMARY KEY,
    cedula VARCHAR(50),
    case_number VARCHAR(50),
    audio_path TEXT,
    original_text_path TEXT,
    modified_text_path TEXT
);
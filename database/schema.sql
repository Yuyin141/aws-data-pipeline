CREATE TABLE IF NOT EXISTS file_metadata (
    id BIGSERIAL PRIMARY KEY,
    file_key VARCHAR(500) NOT NULL UNIQUE,
    file_name VARCHAR(255) NOT NULL,
    file_size BIGINT NOT NULL,
    content_type VARCHAR(100) NOT NULL,
    status VARCHAR(50) NOT NULL,
    processed_file_key VARCHAR(500),
    uploaded_at TIMESTAMP NOT NULL,
    processed_at TIMESTAMP,
    CONSTRAINT chk_status CHECK (status IN ('UPLOADED', 'PROCESSING', 'COMPLETED', 'FAILED'))
);

CREATE INDEX idx_file_key ON file_metadata(file_key);
CREATE INDEX idx_status ON file_metadata(status);
CREATE INDEX idx_uploaded_at ON file_metadata(uploaded_at DESC);


ALTER TABLE attachments
    ADD COLUMN IF NOT EXISTS content_type VARCHAR(255);

ALTER TABLE attachments
    ADD COLUMN IF NOT EXISTS file_size BIGINT;

ALTER TABLE attachments
    ADD COLUMN IF NOT EXISTS original_filename VARCHAR(255);

ALTER TABLE attachments
    ADD COLUMN IF NOT EXISTS stored_filename VARCHAR(255);

ALTER TABLE attachments
    ADD COLUMN IF NOT EXISTS storage_path VARCHAR(500);

ALTER TABLE attachments
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP;

ALTER TABLE attachments
    ADD COLUMN IF NOT EXISTS uploaded_by BIGINT;

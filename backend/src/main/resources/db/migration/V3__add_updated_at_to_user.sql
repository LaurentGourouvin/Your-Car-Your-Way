ALTER TABLE "user" ADD COLUMN updated_at TIMESTAMP;
UPDATE "user" SET updated_at = created_at;
DROP TABLE IF EXISTS doge_photo CASCADE
DROP TABLE IF EXISTS doge_user CASCADE
DROP SEQUENCE IF EXISTS doge_seq
CREATE TABLE doge_user (id SERIAL NOT NULL, name VARCHAR(255), username VARCHAR(255), PRIMARY KEY (id))
CREATE TABLE doge_photo (id SERIAL NOT NULL, data BYTEA, uuid VARCHAR(255), doge_user BIGINT, PRIMARY KEY (id))
ALTER TABLE doge_user ADD CONSTRAINT uk_doge_user_username UNIQUE (username)
ALTER TABLE doge_photo ADD CONSTRAINT uk_doge_photo_uuid UNIQUE (uuid)
ALTER TABLE doge_photo ADD CONSTRAINT fk_doge_user_doge_user FOREIGN KEY (doge_user) REFERENCES doge_user

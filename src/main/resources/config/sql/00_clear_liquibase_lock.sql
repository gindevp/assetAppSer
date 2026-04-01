CREATE TABLE IF NOT EXISTS databasechangeloglock (
  id INT NOT NULL,
  locked BIT(1) NOT NULL,
  lockgranted DATETIME NULL,
  lockedby VARCHAR(255) NULL,
  CONSTRAINT pk_databasechangeloglock PRIMARY KEY (id)
);

INSERT IGNORE INTO databasechangeloglock (id, locked, lockgranted, lockedby)
VALUES (1, b'0', NULL, NULL);

UPDATE databasechangeloglock
SET locked = b'0', lockgranted = NULL, lockedby = NULL
WHERE id = 1;


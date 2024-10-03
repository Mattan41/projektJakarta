CREATE TABLE movie (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    uuid BINARY(16) NOT NULL UNIQUE,
    title VARCHAR(100) NOT NULL,
    releaseYear INT,
    director VARCHAR(50),
    rating FLOAT,
    genre VARCHAR(20)
);

CREATE INDEX idx_uuid ON movie (uuid);

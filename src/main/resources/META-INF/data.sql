INSERT INTO movie (uuid, title, releaseYear, director, rating, genre)
VALUES (UUID_TO_BIN(UUID()), 'The Shawshank Redemption', 1994, 'Frank Darabont', 9.3, 'Drama'),
       (UUID_TO_BIN(UUID()), 'The Godfather', 1972, 'Francis Ford Coppola', 9.2, 'Crime'),
       (UUID_TO_BIN(UUID()), 'The Dark Knight', 2008, 'Christopher Nolan', 9.0, 'Action'),
       (UUID_TO_BIN(UUID()), 'Pulp Fiction', 1994, 'Quentin Tarantino', 8.9, 'Crime'),
       (UUID_TO_BIN(UUID()), 'Forrest Gump', 1994, 'Robert Zemeckis', 8.8, 'Drama'),
       (UUID_TO_BIN(UUID()), 'The Matrix', 1999, 'Lana and Lilly Wachowski', 8.7, 'Action'),
       (UUID_TO_BIN(UUID()), 'Inception', 2010, 'Christopher Nolan', 8.8, 'Sci-Fi'),
       (UUID_TO_BIN(UUID()), 'Titanic', 1997, 'James Cameron', 7.8, 'Romance'),
       (UUID_TO_BIN(UUID()), 'The Social Network', 2010, 'David Fincher', 7.7, 'Biography'),
       (UUID_TO_BIN(UUID()), 'La La Land', 2016, 'Damien Chazelle', 8.0, 'Musical');

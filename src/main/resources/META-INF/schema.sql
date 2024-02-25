CREATE DATABASE movies;

CREATE TABLE movie (
                       Id int NOT NULL AUTO_INCREMENT,
                       title varchar(100),
                       releaseYear int,
                       director varchar(50),
                       rating float,
                       genre char(20),
                       PRIMARY KEY (Id)
); 
DROP TABLE movie;
SELECT * from movie;

# Restful Web Service Implementation with JAX-RS, Jakarta EE, and WildFly
**A group assignment for the course Complex Java Development at ITHS, Gothenburg**

## Build and Run Instructions:

1. **Package the Application**:
   ```bash
   mvn package -DskipTests

2. **Start the Application**:
   ```bash
   docker-compose up --build
   ```

## REST API Endpoints and Expected Formats:

### GET /movies
- **Response**:
  ```json
  {
    "movies": [
      {
        "uuid": "123e4567-e89b-12d3-a456-426614174000",
        "title": "The Shawshank Redemption",
        "director": "Frank Darabont",
        "genre": "Drama",
        "rating": 9.3,
        "releaseYear": 1994
      }
    ],
    "updated": "2023-10-01T12:00:00"
  }
### GET /movies/{uuid}
- **Response**:
  ```json
  {
  "uuid": "123e4567-e89b-12d3-a456-426614174000",
  "title": "The Shawshank Redemption",
  "director": "Frank Darabont",
  "genre": "Drama",
  "rating": 9.3,
  "releaseYear": 1994
  } 

### POST /movies
- **Request**:
  ```json
  {
    "title": "The Shawshank Redemption",
    "director": "Frank Darabont",
    "genre": "Drama",
    "rating": 9.3,
    "releaseYear": 1994
  }
- **Response**:
  ```json
  {
    "message": "Movie successfully added",
    "location": "http://localhost:8080/api/movies/123e4567-e89b-12d3-a456-426614174000"
  }

  
### DELETE /movies/{uuid}
- **Response**:
  ```json
  {
    "message": "Successfully deleted"
  }
  
### PUT /movies/{uuid}
- **Request**:
  ```json
  {
  "title": "The Shawshank Redemption",
  "director": "Frank Darabont",
  "genre": "Drama",
  "rating": 9.3,
  "releaseYear": 1994
  }

- **Response**:
  ```json
  {
  "message": "Successfully updated"
  }      


The group consisted of: Emmelie Johansson, Mats Kruskopf Eriksson, Cristoffer Matlak, and Ludwig Persson
## Key Implementations:
### @Entity Classes:

Define an entity that can be stored in a database table.
### Repository Class:

Implement a repository class with database connectivity.
### Rest API with CRUD Operations:

Develop REST endpoints for Create, Read, Update, and Delete operations for the defined entity.
### JSON Communication:

Exchange information in JSON format between the client and server.
### DTO Usage:

Utilize Data Transfer Objects (DTOs) to avoid direct serialization and deserialization of entity classes.
### Error Handling:

Implement meaningful error handling and use appropriate response codes.
### Exception Handling:

Follow best practices for handling exceptions in JAX-RS applications, as outlined in [Mastertheboss](http://www.mastertheboss.com/jboss-frameworks/resteasy/how-to-handle-exceptions-in-jax-rs-applications/).
### Data Validation:

Implement data validation for incoming data using Jakarta Bean Validation.
### Testing:

Develop comprehensive tests, including endpoint tests, to ensure the reliability of the implemented functionality.
### Dockerization:

Create a Dockerfile for easy deployment of the application as a Docker container.
### Docker-Compose Setup:

Provide a Docker-compose file for starting the application along with the database and WildFly.

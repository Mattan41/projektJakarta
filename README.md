# Restful Web Service Implementation with JAX-RS, Jakarta EE, and WildFly
**A group assignment for the course Complex Java Development at ITHS, Gothenburg**

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

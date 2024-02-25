FROM maven:3.9.6-eclipse-temurin-21-alpine AS  build
COPY ./ /src
RUN mvn -f /src/pom.xml clean package

FROM eclipse-temurin:21.0.2_13-jre-alpine
COPY --from=build /src/target/*.war /projektJakarta.war
EXPOSE 8080
ENTRYPOINT ["java", "-cp", "/projektJakarta.jar", "com.example.projektJakarta.Main"]

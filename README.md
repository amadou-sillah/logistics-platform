# Logistics Backend (Java)
Spring Boot 3.2.2 with MongoDB Atlas and Redis.

## Run
mvn spring-boot:run

## Docker
docker build -t logistics-backend .
docker run -p 8080:8080 -e SPRING_DATA_MONGODB_URI=... logistics-backend

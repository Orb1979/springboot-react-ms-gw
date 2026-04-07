Backend Mono-Repo
=================

This directory contains all backend services and infrastructure:

- `gateway/` – Spring Boot JWT-validating gateway / reverse proxy
- `customer/` – Spring Boot CRUD API (PostgreSQL + JPA/Hibernate + Flyway)
- `nginx/` – Nginx reverse proxy config + Dockerfile
- `docker-compose.yml` – Local stack: Postgres, nginx, and (optionally) the Spring Boot services

Services
--------

- **Gateway-service**
  - Validates JWTs issued by Auth0 and forwards `/api/{service}/**` requests to downstream services.

- **Customer-service**
  - Simple CRUD rest api

Docker Compose
--------------
- `docker-compose.yml` - postgresSql  
- `docker-compose-dev-nginx.yml` - postgresSql + nginx (nginx forwards directly to microservice)
- `docker-compose-dev-nginx-gw.yml` - postgresSql + nginx (nginx forwards through gateway service)
- `docker-compose-stg.yml` -postgresSql + nginx container + microservices


During local development you can:
- Run Spring Boot apps directly from your IDE or Maven/Gradle
- Run the DB + nginx via `docker compose up -d`
- Hit the services either:
  - Directly (`http://localhost:8080`, `http://localhost:8081`), or
  - Via nginx (`http://localhost/` and `/api/**`)


# Salary Management

Salary Management is a full-stack application for HR managers to view employee salary records, update salaries, and review salary history. It is designed around a seeded employee dataset and a Spring Boot API consumed by an Angular frontend.

[Demo Video](https://drive.google.com/file/d/1Lyd67cv6HSHb_HYf4PykSsXx95iZXCCd/view?usp=sharing)

## Tech Stack

* Angular 22.1.0
* Angular Material 22.1.2
* Bootstrap 5.3.8
* Java 21
* Spring Boot 4.1.0
* MySQL
* Spring Data JPA with Hibernate

## Prerequisites

* Java 21
* Maven installed and available as `mvn`
* Node.js `^22.22.3 || ^24.15.0 || ^26.0.0` with npm
* MySQL running locally

## Database Setup

Create a local MySQL database:

```sql
CREATE DATABASE acme_salary_management;
```

Update the local database connection settings in `backend/src/main/resources/application.properties` if your MySQL URL, username, or password is different.

## Run Backend

From the repository root:

```bash
cd backend
mvn spring-boot:run
```

The backend runs on `http://localhost:8080`.

When the backend starts, the application automatically seeds 10,000 employees and their salary records if the employee table is empty.

## Run Backend Tests

From the repository root:

```bash
cd backend
mvn test
```

## Run Frontend

From the repository root:

```bash
cd frontend
npm install
npm start
```

The frontend runs on `http://localhost:4200` and proxies `/api` requests to `http://localhost:8080`.

## Login

Local HR credentials:

* Email: `hr@acme.com`
* Password: `admin123`

## Documentation

* [Requirements](docs/requirements.md)
* [Architecture](docs/architecture.md)
* [Design Notes](docs/design-notes.md)
* [AI Usage](docs/ai-usage.md)

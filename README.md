# 🎬 Movie & Director REST Service

**This web application was developed as part of the Block 2 assignment (Spring Boot & REST). It is designed to manage a database of movies and directors, providing a robust REST API for CRUD operations, complex filtering, CSV reporting, and batch data import.**

## 🌟 Project Overview
**Goal:** Develop a Spring Boot service that stores data in PostgreSQL, exposes endpoints for managing entities, handles relationship integrity, and generates file-based reports.

#### Core Data Model
| Entity | Attributes | Relationship |
| ----- |:-------------:|:-------------:|
| Movie | _id, title, release_year, genre, director_id_      | Child entity. Belongs to one Director.|
| Director | _id, name_  | Parent entity. One director can have many movies.|

## 🛠️ Technology Stack
- Java 21 & Spring Boot 4: Core framework for DI, Web MVC, and Data access.
- PostgreSQL: Relational database for persistent storage.
- Liquibase: Database schema migration management (DDL).
- MapStruct: Efficient, type-safe mapping between Entities and DTOs.
- SpringDoc (Swagger UI): Automated API documentation and testing interface.
- JUnit 5 & Mockito: Unit and Integration testing layers.

## ⚙️ How to Run the Application
The application is built using Maven.

**Requirements**
- Java 21+
- Maven 3.8+
- PostgreSQL (local or Docker)

**Configuration:**
Before running, create a database named **_rest_service_** or something else and update _src/main/resources/application.properties_ if your credentials different from the defaults (_postgres/root_).

```sql
    CREATE DATABASE rest_service;
```

**Execution Command**
```
mvn spring-boot:run
```
Once started, the server will be available at http://localhost:8080.
|URL|	Description|
| ------------- |:-------------:|
|localhost:8080/swagger-ui/index.html#/|Interactive API Documentation (Swagger UI)|

## 📊 Supported Features
#### The API supports the following key operations:

**CRUD Operations:** Full management of Movies and Directors.

**Filtering:** Advanced search by Director, Genre, and Release Year with pagination support.

**Reporting:** Generation of downloadable CSV reports based on search criteria.

**Import:** Bulk creation of movies from JSON files with error handling.

## 🔌 General Endpoints

### 👤 Directors

| Method | URL | Description |
| :--- | :--- | :--- |
| `GET` | `/api/director` | Get all directors |
| `POST` | `/api/director` | Create new director |
| `PUT` | `/api/director/{id}` | Update director |
| `DELETE` | `/api/director/{id}` | Delete director |

### 🎬 Movies

| Method | URL | Description |
| :--- | :--- | :--- |
| `GET` | `/api/movie` | Get list of movies |
| `GET` | `/api/movie/{id}` | Get movie details |
| `POST` | `/api/movie` | Create movie |
| `PUT` | `/api/movie/{id}` | Update movie |
| `DELETE` | `/api/movie/{id}` | Delete movie |

### 🔍 Additional abilities

#### 1. Filtration and pagination
**POST** `/api/movie/_list`
body of request (all fields are optional):
```json
{
  "directorId": 1,
  "genre": "SCI_FI",
  "releaseYear": 2010,
  "page": 0,
  "size": 10
}
```
#### 2. Report generation (CSV)
**POST** `/api/movie/_report` tooks all filters, such a __list_. Returns _.csv_ for download.
#### Output fragment of File _movies_report.csv:_
```csv
ID,Title,Year,Genre,Director
1,"Oppenheimer",2023,DRAMA,"Christopher Nolan"
2,"Interstellar",2014,SCI_FI,"Christopher Nolan"
```
#### 3. Import files
**POST** `/api/movie/upload` tooks file (multipart/form-data, key=file) from JSON array:
```json
[
  {
    "title": "Inception",
    "releaseYear": 2010,
    "genre": "SCI_FI",
    "directorId": 1
  },
  {
    "title": "The Godfather",
    "releaseYear": 1972,
    "genre": "DRAMA",
    "directorId": 2
  }
]
```
## 📬 Postman Collection
For convenient manual testing and exploring of endpoints, a complete Postman collection is available. It includes pre-configured requests for all implemented features (CRUD, Upload, Reports).

👉 [View Postman Collection](https://sergii-838248.postman.co/workspace/Sergii's-Workspace~cc6177e6-e3a2-4ef1-8b2a-04bc61c70757/collection/43376300-c9c6c716-9b69-4691-8013-db2b7167459e?action=share&creator=43376300)
## 🧪 Testing & Quality Assurance
To ensure the reliability of the application, comprehensive testing was performed using JUnit 5 and Mockito.

| Test Type | Scope | Coverage Goal |
| :--- | :---: | :--- |
| **Unit Tests** | Service Layer | Business logic, duplicates check, exception throwing. |
| **Integration Tests** | Controller Layer (`@WebMvcTest`) | HTTP status codes, JSON serialization, Global Exception Handling, Validation (`@Valid`). |

## Analysis and Conclusion
### Conclusion:
The project successfully implements a layered architecture (Controller -> Service -> Repository).

* **Data Integrity:** Enforced via Database constraints (Foreign Keys) and Application logic (Custom Exceptions).

* **Scalability:** The use of Pageable and DTOs ensures that the application handles large datasets efficiently without exposing internal entity structures.

* **Usability:** Swagger UI and Postman Collections provide immediate ways for frontend developers or QA engineers to interact with the API without writing external scripts.
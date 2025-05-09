# 🚀 Subscription Service

A **Spring Boot**-based microservice that allows clients to manage **feature flags** efficiently. Designed with modularity, scalability, and extensibility in mind — perfect for toggling features on a per-client basis.

---

## 🧩 Features

- ✅ **Client Management**: Add and manage clients.
- 🎚️ **Feature Flags**: Enable/disable features for individual clients.
- 📊 **Parent-Child Feature Rules**: Automatically enable/disable child features when parent is toggled.
- 🔐 **Per-client Feature Visibility**: Get all enabled features for a specific client.
- ⚠️ **Global Exception Handling**: Clean, consistent error responses.

---

## 🛠️ Tech Stack

| Tech        | Description                              |
|-------------|------------------------------------------|
| Java 17     | Primary programming language             |
| Spring Boot | Backend framework                        |
| Maven       | Build & dependency management            |
| Docker      | Containerization                         |
| JPA         | ORM for database operations              |
| H2 DB       | (Optional) In-memory DB for local dev    |

---

## 🚀 Getting Started

### ✅ Prerequisites

- Java 17+
- Maven 3.8.3+
- Docker (optional, for container deployment)

---

## 📦 Application Setup
### 🔨 Build the App

```bash
mvn clean package -DskipTests
```

▶️ Run the App in local
```bash
java -jar target/subscriptionservice-0.0.1-SNAPSHOT.jar
```




### 🐳 Docker Setup
```bash
docker build -t subscription-service:latest . 
docker run -p 8080:8080 --name subscription-app subscription-service:latest
```

###🐳 Docker Compose Setup
```bash
docker-compose up --build
```

### 🌐 Access the API
Postman collection is available in the `resource` directory. Import it to test the endpoints.


### 📜 Database Endpoint
```bash
localhost:8080/h2-console
jdbc:h2:mem:testdb
username: sa
password: 
```

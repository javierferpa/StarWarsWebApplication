# Star Wars Web Application

A modern web application that provides an interactive interface for exploring Star Wars data from the SWAPI (Star Wars API). Built with an Angular and Spring Boot microservices architecture, featuring advanced search, sorting, and pagination capabilities.

[![CI/CD Pipeline](https://github.com/javierferpa/StarWarsWebApplication/actions/workflows/ci-cd.yml/badge.svg)](https://github.com/javierferpa/StarWarsWebApplication/actions/workflows/ci-cd.yml)
[![Live Demo](https://img.shields.io/badge/demo-live-brightgreen)](https://starwars-frontend-production.up.railway.app/people)

## 🚀 Live Demo

- **Application**: [https://starwars-frontend-production.up.railway.app/people](https://starwars-frontend-production.up.railway.app/people)
- **API Health Check**: [https://starwars-backend-production.up.railway.app/actuator/health](https://starwars-backend-production.up.railway.app/actuator/health)
- **API Documentation**: [https://starwars-backend-production.up.railway.app/api/people](https://starwars-backend-production.up.railway.app/api/people)

## ✨ Features

- **People Directory**: Browse and search Star Wars characters with detailed information.
- **Planets Database**: Explore Star Wars planets and their characteristics.
- **Advanced Pagination**: Server-side pagination with 15 items per page for optimal performance.
- **Smart Search**: Real-time case-insensitive partial name matching.
- **Flexible Sorting**: Multi-criteria sorting by name, creation date, height, and population.
- **Responsive Design**: Modern Material Design interface optimized for all devices.

## 🛠 Technology Stack

### Frontend
- **Angular 20**: Latest Angular framework with standalone components.
- **Angular Material**: Modern UI components with Star Wars theming.
- **TypeScript**: Type-safe development with strict compilation.
- **SCSS**: Advanced styling with component-scoped CSS.

### Backend
- **Spring Boot 3.5**: Modern Java framework with reactive programming.
- **Java 17**: Latest LTS Java version.
- **WebClient**: Non-blocking HTTP client for external API calls.
- **Caffeine Cache**: High-performance in-memory caching.
- **Maven**: Dependency management and build automation.

### Infrastructure
- **Docker & Docker Compose**: Containerization for consistent development and production environments.
- **Nginx**: Production-grade reverse proxy and static file serving.
- **Railway**: Cloud deployment platform with private networking.
- **GitHub Actions**: CI/CD pipeline for automated testing and deployment.
- **SWAPI**: External Star Wars API data source.

## 🚦 Quick Start

1.  **Prerequisites**:
    - **Docker Desktop** (v20.0+)
    - **Git**
    - At least 4GB RAM available for containers.
    - Ports `6969` and `8080` must be free.

2.  **Clone and start the application:**
    ```bash
    git clone https://github.com/javierferpa/StarWarsWebApplication.git
    cd StarWarsWebApplication
    docker-compose up -d --build
    ```
    > **Note:** The initial build may take 3-5 minutes while Maven and Node.js dependencies are downloaded and compiled.

3.  **Access the application:**
    - **🌐 Frontend**: [http://localhost:6969](http://localhost:6969)
    - **📡 Backend API**: [http://localhost:8080/api/people](http://localhost:8080/api/people)

> 👉 **If something fails, please check the [Troubleshooting](#-troubleshooting) section.**

## 📡 API Documentation

The API provides endpoints for `people` and `planets`.

### Query Parameters

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `page` | integer | 0 | Page number (zero-based) |
| `size` | integer | 15 | Items per page (max 100) |
| `search` | string | - | Case-insensitive partial name search |
| `sort` | string | name | Sort field: `name`, `created`, `height`, `population` |
| `dir` | string | asc | Sort direction: `asc`, `desc` |

### Example Request

```bash
# Get the second page of planets, sorted by population descending
curl "http://localhost:8080/api/planets?page=1&sort=population&dir=desc"
```

### Response Format

```json
{
  "content": [...],
  "page": {
    "size": 15,
    "number": 1,
    "totalElements": 60,
    "totalPages": 4
  }
}
```

## 🏗 Architecture & Performance

The application uses a **microservices architecture** with a decoupled frontend and backend.

### Key Design Principles
- **Backend**: Implements the **Strategy Pattern** for flexible sorting, ensuring the system is open for extension but closed for modification. A **Repository Pattern** abstracts data access, and non-blocking I/O is handled with **WebClient**.
- **Frontend**: Follows a **Smart/Dumb Component** architecture, uses **RxJS** for reactive state management, and lazy loads feature modules.

### Performance Optimizations
- **Backend**: High-performance **Caffeine Cache** reduces latency from the external SWAPI. Server-side pagination minimizes payload size.
- **Frontend**: **Lazy Loading** of routes, **OnPush Change Detection**, and `trackBy` functions ensure a smooth user experience.

## ☁️ Cloud Deployment

The application is deployed on **Railway**, leveraging its container platform for scalability and reliability.

- **Infrastructure**: The `starwars-frontend` (Nginx) and `starwars-backend` (Spring Boot) services run in separate containers.
- **Private Networking**: Services communicate over a secure private network, with the frontend proxying requests to the backend.
- **CI/CD**: A **GitHub Actions** workflow automates testing and deployment. Pushes to the `develop` branch trigger a deployment to the production environment on Railway. For more details, see the [`.github/workflows/ci-cd.yml`](.github/workflows/ci-cd.yml) file.

## 🔧 Development

### Local Development
For development with hot-reload:

- **Backend**: Navigate to `BackEnd/` and run `./mvnw spring-boot:run`.
- **Frontend**: Navigate to `frontend/` and run `npm install && npm start`.

### Testing
- **Backend**: `cd BackEnd && ./mvnw test` (30 tests)
- **Frontend**: `cd frontend && npm test` (7 tests)

## 🤝 Contributing

Contributions are welcome. Please fork the repository, create a feature branch, and submit a pull request. Ensure all tests pass and adhere to the existing code style.

## 🔧 Troubleshooting

### Port Conflicts
If ports `6969` or `8080` are in use, stop the conflicting application or change the ports in `docker-compose.yml`.

### Build Issues
If you encounter build problems, try a clean rebuild:
```bash
docker-compose down --volumes --rmi all
docker-compose up -d --build --no-cache
```

### Log Analysis
To view logs for all services or a specific one:
```bash
# View all logs in real-time
docker-compose logs -f

# View logs for a specific service
docker-compose logs backend
docker-compose logs frontend
```

## 📋 Requirements Compliance

This project was developed to meet a specific set of [functional and technical requirements](./docs/requirements.md), which are documented separately.

## 📄 License

This project was created for educational and technical assessment purposes. It is licensed under the MIT License. See the [LICENSE](./LICENSE) file for details.

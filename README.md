# Star Wars Web Application

A full-stack web application that displays Star Wars data from the SWAPI (Star Wars API) using Angular frontend and Spring Boot backend.

[![CI/CD Pipeline](https://github.com/javierferpa/StarWarsWebApplication/actions/workflows/ci-cd.yml/badge.svg)](https://github.com/javierferpa/StarWarsWebApplication/actions/workflows/ci-cd.yml)
[![Live Demo](https://img.shields.io/badge/demo-live-brightgreen)](https://starwars-app-production.up.railway.app)

## Live Demo
- **Application**: [https://starwars-app-production.up.railway.app](https://starwars-app-production.up.railway.app)
- **API Health Check**: [https://starwars-backend-production.up.railway.app/actuator/health](https://starwars-backend-production.up.railway.app/actuator/health)

## Features

- **People Table**: Browse and search Star Wars characters
- **Planets Table**: Browse and search Star Wars planets
- **Pagination**: 15 items per page with server-side handling
- **Search**: Real-time case-insensitive partial name matching
- **Sorting**: Sort by name, created date, height, or population (ascending/descending)
- **Responsive Design**: Modern Material Design UI with Star Wars theming

## Technology Stack

- **Frontend**: Angular 20 with Angular Material
- **Backend**: Spring Boot 3.5 with Java 17
- **Containerization**: Docker & Docker Compose
- **Data Source**: External SWAPI (Star Wars API)

## Quick Start

### Prerequisites

- Docker and Docker Compose installed
- At least 4GB of available RAM
- Ports 6969 and 8080 available

### Running the Application

1. **Clone the repository:**
   ```bash
   git clone https://github.com/javierferpa/StarWarsWebApplication.git
   cd StarWarsWebApplication
   ```

2. **Start the application with Docker Compose:**
   ```bash
   docker-compose up -d --build
   ```
   
   The Docker build process will automatically:
   - Build the Spring Boot JAR file using Maven
   - Create optimized Docker images for both services
   - Start the application with health checks

3. **Access the application:**
   - Frontend: [http://localhost:6969](http://localhost:6969)
   - Backend API: [http://localhost:8080](http://localhost:8080)
   - Health Check: [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)

### Stopping the Application

```bash
docker-compose down
```

## Development

### Local Development Setup

#### Backend (Spring Boot)
```bash
cd BackEnd
./mvnw spring-boot:run
```

#### Frontend (Angular)
```bash
cd frontend
npm install
npm start
```

### Running Tests

#### Backend Tests
```bash
cd BackEnd
./mvnw test
```

#### Frontend Tests
```bash
cd frontend
npm test
```

## API Endpoints

- `GET /api/people` - Get people with pagination, search, and sorting
- `GET /api/planets` - Get planets with pagination, search, and sorting

### Query Parameters
- `page`: Page number (0-based, default: 0)
- `size`: Items per page (default: 15)
- `search`: Search term for name filtering
- `sort`: Field to sort by (name, created, height, population)
- `dir`: Sort direction (asc, desc, default: asc)

## Architecture

The application follows a microservices architecture with clean separation of concerns:

- **Frontend Service**: Angular SPA served by Nginx with reverse proxy configuration
- **Backend Service**: Spring Boot REST API with reactive WebClient for external API calls
- **Communication**: HTTP REST calls from frontend to backend via nginx proxy
- **Data Source**: External SWAPI with intelligent caching and fallback strategies

### Key Design Patterns

- **Strategy Pattern**: Flexible sorting system supporting multiple criteria
- **Reactive Programming**: Non-blocking HTTP calls with WebClient
- **Caching**: Caffeine-based caching for improved performance
- **Error Resilience**: Comprehensive error handling with fallback mechanisms

## Docker Configuration

Multi-stage Docker builds with automatic compilation:

- **Frontend**: Node.js build stage + Nginx runtime with custom reverse proxy configuration
- **Backend**: Maven build stage automatically compiles source code + JRE runtime stage
- **No Pre-build Required**: JAR files are compiled automatically during Docker build process
- **Health Checks**: Automatic service health monitoring with dependency management
- **Networking**: Internal service communication with external port mapping

## Project Structure

```
StarWarsWebApplication/
├── BackEnd/                 # Spring Boot backend
│   ├── src/
│   │   ├── main/java/com/starwars/backend/
│   │   │   ├── client/      # SWAPI HTTP client
│   │   │   ├── config/      # Configuration classes
│   │   │   ├── controller/  # REST controllers
│   │   │   ├── model/       # DTOs and data models
│   │   │   ├── service/     # Business logic
│   │   │   ├── sorting/     # Sorting strategies
│   │   │   └── util/        # Utility classes
│   │   └── resources/
│   ├── pom.xml
│   └── Dockerfile
├── frontend/                # Angular frontend
│   ├── src/
│   │   ├── app/
│   │   │   ├── core/        # Core services and interceptors
│   │   │   ├── features/    # Feature modules (people, planets)
│   │   │   └── shared/      # Shared components
│   │   └── environments/
│   ├── package.json
│   └── Dockerfile
├── docker-compose.yml       # Docker orchestration
└── README.md
```

## Troubleshooting

### Common Issues

1. **Port conflicts**: Ensure ports 6969 and 8080 are available
2. **Memory issues**: Increase Docker memory allocation to at least 4GB
3. **Health check failures**: Wait up to 2 minutes for services to fully start

### Logs

View application logs:
```bash
docker-compose logs -f
docker-compose logs backend
docker-compose logs frontend
```

### Development Tips

- Backend logs show detailed API interaction and caching behavior
- Frontend errors appear in browser console and Docker logs
- Health checks ensure proper service startup ordering

## CI/CD Pipeline

The project includes a complete CI/CD pipeline using GitHub Actions:

### Continuous Integration
- **Automated testing** for both frontend and backend
- **Code quality checks** and linting
- **Security scanning** with Trivy
- **Docker image building** and caching

### Continuous Deployment
- **Automatic deployment** to Railway on main branch
- **Staging environment** on develop branch
- **Container registry** using GitHub Container Registry
- **Health check validation** before deployment

### Pipeline Stages
1. **Test Stage**: Run all unit tests for both services
2. **Build Stage**: Create optimized Docker images
3. **Security Stage**: Scan for vulnerabilities
4. **Deploy Stage**: Deploy to Railway with zero downtime

### Environments
- **Production**: `main` branch → Railway Production
- **Staging**: `develop` branch → Railway Staging
- **Feature branches**: Run tests only

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Run tests locally
5. Submit a pull request

## License

This project is for educational purposes.
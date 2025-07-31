# Star Wars Web Application

A modern web application that provides an interactive interface for exploring Star Wars data from the SWAPI (Star Wars API). Built with Angular and Spring Boot microservices architecture, featuring advanced search, sorting, and pagination capabilities.

[![CI/CD Pipeline](https://github.com/javierferpa/StarWarsWebApplication/actions/workflows/ci-cd.yml/badge.svg)](https://github.com/javierferpa/StarWarsWebApplication/actions/workflows/ci-cd.yml)
[![Live Demo](https://img.shields.io/badge/demo-live-brightgreen)](https://starwars-frontend-production.up.railway.app/people)

## 🚀 Live Demo

- **Application**: [https://starwars-frontend-production.up.railway.app/people](https://starwars-frontend-production.up.railway.app/people)
- **API Health Check**: [https://starwars-backend-production.up.railway.app/actuator/health](https://starwars-backend-production.up.railway.app/actuator/health)
- **API Documentation**: [https://starwars-backend-production.up.railway.app/api/people](https://starwars-backend-production.up.railway.app/api/people)

**⚡ TL;DR - Run in 30 seconds:**
```bash
git clone https://github.com/javierferpa/StarWarsWebApplication.git
cd StarWarsWebApplication  
docker-compose up -d --build
# Wait 2-3 minutes for build, then open: http://localhost:6969
```

**📋 Evaluation Checklist:**
- ✅ **No local dependencies** required (only Docker)
- ✅ **One-command setup** with automatic dependency resolution
- ✅ **Health checks** ensure services are ready before access
- ✅ **Comprehensive testing** - backend (30 tests) + frontend (7 tests)
- ✅ **Production deployment** - live demo available above


## ✨ Features

### Core Functionality
- **People Directory**: Browse and search Star Wars characters with detailed information
- **Planets Database**: Explore Star Wars planets and their characteristics
- **Advanced Pagination**: Server-side pagination with 15 items per page for optimal performance
- **Smart Search**: Real-time case-insensitive partial name matching across all fields
- **Flexible Sorting**: Multi-criteria sorting by name, creation date, height, and population (ascending/descending)
- **Responsive Design**: Modern Material Design interface optimized for all devices

### Technical Features
- **Microservices Architecture**: Decoupled frontend and backend services
- **Container-First Deployment**: Docker and Docker Compose for consistent environments
- **Production-Ready CI/CD**: Automated testing and deployment pipeline
- **Cloud Deployment**: Railway platform integration with private networking
- **Performance Optimization**: Intelligent caching and lazy loading
- **Error Resilience**: Comprehensive error handling with fallback mechanisms

## 🛠 Technology Stack

### Frontend
- **Angular 20**: Latest Angular framework with standalone components
- **Angular Material**: Modern UI components with Star Wars theming
- **TypeScript**: Type-safe development with strict compilation
- **SCSS**: Advanced styling with component-scoped CSS
- **Nginx**: Production-grade reverse proxy and static file serving

### Backend  
- **Spring Boot 3.5**: Modern Java framework with reactive programming
- **Java 17**: Latest LTS Java version with advanced features
- **WebClient**: Non-blocking HTTP client for external API calls
- **Caffeine Cache**: High-performance in-memory caching
- **Maven**: Dependency management and build automation

### Infrastructure
- **Docker**: Containerization with multi-stage builds
- **Docker Compose**: Local development orchestration
- **Railway**: Cloud deployment platform with private networking
- **GitHub Actions**: CI/CD pipeline with automated testing
- **SWAPI**: External Star Wars API data source

## 🚦 Quick Start

### Prerequisites

**Required Software:**
- **Docker Desktop** (version 20.0 or higher) - [Download here](https://www.docker.com/products/docker-desktop/)
- **Docker Compose** (included with Docker Desktop)
- **Git** - [Download here](https://git-scm.com/)

**System Requirements:**
- **At least 4GB RAM** available for containers
- **Ports 6969 and 8080** must be available on your system

### One-Command Setup

1. **Clone and start the application:**

   ```bash
   git clone https://github.com/javierferpa/StarWarsWebApplication.git
   cd StarWarsWebApplication
   docker-compose up -d --build
   ```

   **What happens during build:**
   - ⏳ Backend: Maven compiles Spring Boot application (~2-3 minutes)
   - ⏳ Frontend: Node.js builds Angular application (~3-4 minutes)
   - 🐳 Docker creates optimized production images
   - 🚀 Services start with health checks and dependencies

2. **Wait for services to be ready:**

   ```bash
   # Monitor the startup process (optional)
   docker-compose logs -f
   ```

   **Look for these success messages:**
   - Backend: `Started BackEndApplication in X.XXX seconds`
   - Frontend: `Configuration complete. NGINX server started`

3. **Access the application:**

   - **🌐 Frontend**: [http://localhost:6969](http://localhost:6969) _(Main Application)_
   - **📡 Backend API**: [http://localhost:8080/api/people](http://localhost:8080/api/people) _(API Endpoint)_
   - **❤️ Health Check**: [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health) _(Service Status)_

### Troubleshooting

**Port conflicts:**
```bash
# Check if ports are in use
netstat -an | findstr :6969  # Windows
netstat -an | grep :6969     # Linux/Mac

# Stop existing containers
docker-compose down
```

**Build issues:**
```bash
# Clean rebuild
docker-compose down --volumes --rmi all
docker-compose up -d --build --no-cache
```

**View logs:**
```bash
# All services
docker-compose logs

# Specific service
docker-compose logs backend
docker-compose logs frontend
```

### Stopping the Application

```bash
docker-compose down
```

## 🔧 Development

### Local Development Setup

For development with hot reload and debugging capabilities:

#### Backend Development
```bash
cd BackEnd
./mvnw spring-boot:run
```
- Runs on port 8080 with Spring Boot DevTools
- Automatic restart on code changes
- H2 console available at `/h2-console` (if configured)

#### Frontend Development  
```bash
cd frontend
npm install
npm start
```
- Runs on port 4200 with live reload
- Proxies API calls to localhost:8080 via `proxy.conf.json`
- Source maps enabled for debugging

### Testing

#### Backend Tests (30 tests)
```bash
cd BackEnd
./mvnw test
```

**Test Coverage Includes:**
- Unit tests for all controllers (PeopleController, PlanetsController)
- Service layer tests with mocked external API calls
- Sorting strategy tests for all supported criteria
- Integration tests for complete request flows
- Error handling and edge case validation

#### Frontend Tests (7 tests with 35%+ coverage)
```bash
cd frontend  
npm test
```

**Test Coverage Includes:**
- Component unit tests for all major components
- Service tests with HTTP client mocking
- Integration tests for user interactions
- Routing and navigation tests

#### End-to-End Testing
```bash
# Run both services then execute
cd frontend
npm run e2e
```

## 📡 API Documentation

### Endpoints

#### People Endpoint
```
GET /api/people
```

#### Planets Endpoint  
```
GET /api/planets
```

### Query Parameters

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `page` | integer | 0 | Page number (zero-based) |
| `size` | integer | 15 | Items per page (max 100) |
| `search` | string | - | Case-insensitive partial name search |
| `sort` | string | name | Sort field: `name`, `created`, `height`, `population` |
| `dir` | string | asc | Sort direction: `asc`, `desc` |

### Example Requests

```bash
# Get first page of people
curl "http://localhost:8080/api/people"

# Search for characters containing "sky"
curl "http://localhost:8080/api/people?search=sky"

# Get planets sorted by population (descending)
curl "http://localhost:8080/api/planets?sort=population&dir=desc"

# Complex query with pagination and search
curl "http://localhost:8080/api/people?page=1&size=10&search=dar&sort=created&dir=desc"
```

### Response Format

```json
{
  "content": [...],
  "page": {
    "size": 15,
    "number": 0,
    "totalElements": 82,
    "totalPages": 6
  }
}
```

## 🏗 Architecture

### System Design

The application follows a **microservices architecture** with clear separation of concerns:

```
┌─────────────────┐    HTTP/REST    ┌─────────────────┐    HTTP/REST    ┌─────────────────┐
│                 │ ──────────────> │                 │ ──────────────> │                 │
│   Angular SPA   │                 │ Spring Boot API │                 │   SWAPI.dev     │
│   (Frontend)    │ <────────────── │   (Backend)     │ <────────────── │  (External API) │
│                 │    JSON Data    │                 │    JSON Data    │                 │
└─────────────────┘                 └─────────────────┘                 └─────────────────┘
        │                                   │
        ▼                                   ▼
┌─────────────────┐                 ┌─────────────────┐
│  Nginx Proxy    │                 │ Caffeine Cache  │
│  Static Assets  │                 │ Error Handling  │
└─────────────────┘                 └─────────────────┘
```

### Key Design Principles

#### Frontend Architecture
- **Modular Design**: Feature-based modules with lazy loading
- **Reactive Programming**: RxJS observables for data flow management
- **State Management**: Service-based state with BehaviorSubjects
- **Component Architecture**: Smart/dumb component pattern
- **Error Boundaries**: Comprehensive error handling with user feedback

#### Backend Architecture  
- **Strategy Pattern**: Pluggable sorting implementations following Open-Closed Principle
- **Repository Pattern**: Clean data access abstraction
- **Reactive Programming**: Non-blocking I/O with WebClient
- **Caching Strategy**: Multi-layered caching with TTL management
- **Circuit Breaker**: Resilience patterns for external API calls

### Performance Optimizations

#### Frontend Optimizations
- **Lazy Loading**: Route-based code splitting
- **OnPush Change Detection**: Optimized Angular change detection
- **TrackBy Functions**: Efficient list rendering
- **Image Optimization**: Responsive images with lazy loading
- **Bundle Optimization**: Tree shaking and dead code elimination

#### Backend Optimizations
- **Connection Pooling**: Optimized HTTP client configuration  
- **Response Caching**: Intelligent caching with cache invalidation
- **Pagination**: Server-side pagination to reduce payload size
- **Async Processing**: Non-blocking request handling
- **JVM Tuning**: Optimized garbage collection and memory usage

## ☁️ Cloud Deployment

### Railway Platform

The application is deployed on Railway using their advanced containerization platform:

#### Deployment Architecture
```
Internet ──> Railway Load Balancer ──> Frontend Service (Nginx)
                                           │
                                           ▼
                              Private Network ──> Backend Service (Spring Boot)
                                           │
                                           ▼
                                    External SWAPI.dev
```

#### Service Configuration

**Backend Service** (`starwars-backend`):
- **Root Directory**: `BackEnd/`
- **Build**: Automatic Dockerfile detection
- **Port**: 8080 (auto-detected from Spring Boot)
- **Environment**: Production profile with optimized JVM settings
- **Scaling**: Auto-scaling based on CPU and memory usage
- **Health Checks**: Built-in monitoring with `/actuator/health`

**Frontend Service** (`starwars-frontend`):  
- **Root Directory**: `frontend/`
- **Build**: Multi-stage Docker build with Nginx
- **Port**: 80 (Nginx reverse proxy)
- **Environment**: Production build with API proxy configuration
- **CDN**: Railway's built-in CDN for static assets
- **SSL**: Automatic HTTPS with Railway-managed certificates

#### Railway Features Utilized

- **Private Networking**: Internal service communication via `starwars-backend.railway.internal:8080`
- **Zero Downtime Deployments**: Rolling updates with health check validation
- **Automatic Scaling**: Resource-based scaling with defined limits
- **Monitoring**: Built-in metrics, logging, and alerting
- **Custom Domains**: Support for custom domain configuration
- **Environment Management**: Separate staging and production environments

### CI/CD Pipeline

#### GitHub Actions Workflow

The project includes a comprehensive CI/CD pipeline that ensures code quality and automated deployment:

```yaml
Trigger: Push to main/develop branches
├── Test Stage
│   ├── Backend Tests (30 tests)
│   ├── Frontend Tests (7 tests)  
│   └── Test Coverage Report
├── Build Stage
│   ├── Docker Image Build
│   ├── Multi-platform Support
│   └── Registry Push (GHCR)
├── Security Stage
│   ├── Vulnerability Scanning
│   ├── Dependency Audit
│   └── SAST Analysis
└── Deploy Stage
    ├── Railway Deployment
    ├── Health Check Validation
    └── Rollback on Failure
```

#### Pipeline Features

- **Automated Testing**: All tests must pass before deployment
- **Security Scanning**: Trivy vulnerability scanning for containers
- **Multi-stage Builds**: Optimized Docker images with layer caching
- **Deployment Validation**: Health checks ensure successful deployment
- **Rollback Strategy**: Automatic rollback on deployment failure
- **Notification**: Slack/email notifications for pipeline status

#### Deployment Strategies

1. **Branch-based Deployment**:
   - `main` branch → Production environment
   - `develop` branch → Staging environment
   - Feature branches → Review apps (optional)

2. **Manual Deployment**:
   ```bash
   # Deploy to Railway manually
   railway login
   railway link [project-id]
   railway up
   ```

3. **Docker Compose Import**:
   - Upload `railway-compose.yml` to Railway dashboard
   - Automatic service creation and configuration

## 📁 Project Structure

```
StarWarsWebApplication/
├── 📁 BackEnd/                          # Spring Boot microservice
│   ├── 📁 src/main/java/com/starwars/backend/
│   │   ├── 📁 api/                      # External API integration
│   │   ├── 📁 client/                   # HTTP clients and API connectors
│   │   ├── 📁 config/                   # Configuration classes
│   │   ├── 📁 controller/               # REST controllers
│   │   ├── 📁 model/                    # DTOs and data models
│   │   ├── 📁 service/                  # Business logic layer
│   │   ├── 📁 sorting/                  # Strategy pattern sorting implementations
│   │   └── 📁 util/                     # Utility classes
│   ├── 📁 src/main/resources/
│   │   ├── 📄 application.properties    # Application configuration
│   │   └── 📄 application-prod.properties # Production overrides
│   ├── 📄 pom.xml                       # Maven dependencies
│   ├── 📄 Dockerfile                    # Multi-stage container build
│   └── 📄 mvnw                          # Maven wrapper
├── 📁 frontend/                         # Angular microservice  
│   ├── 📁 src/
│   │   ├── 📁 app/
│   │   │   ├── 📁 core/                 # Singleton services, guards, interceptors
│   │   │   ├── 📁 features/             # Feature modules
│   │   │   │   ├── 📁 people/           # People management feature
│   │   │   │   └── 📁 planets/          # Planets management feature
│   │   │   └── 📁 shared/               # Shared components and utilities
│   │   │       ├── 📁 data-table/       # Reusable data table component
│   │   │       └── 📁 search-bar/       # Reusable search component
│   │   └── 📁 environments/             # Environment configurations
│   ├── 📄 angular.json                  # Angular workspace configuration
│   ├── 📄 package.json                  # Node.js dependencies
│   ├── 📄 proxy.conf.json               # Development proxy configuration
│   ├── 📄 Dockerfile                    # Multi-stage container build
│   └── 📄 start-nginx.sh                # Production nginx configuration script
├── 📁 .github/workflows/                # CI/CD pipeline
│   └── 📄 ci-cd.yml                     # GitHub Actions workflow
├── 📄 docker-compose.yml                # Local development orchestration
├── 📄 railway-compose.yml               # Railway deployment configuration
└── 📄 README.md                         # This documentation
```

### Architecture Decisions

#### Backend Structure
- **Layered Architecture**: Clear separation between controllers, services, and data access
- **Strategy Pattern**: Sorting implementations follow Open-Closed Principle
- **External API Integration**: Dedicated client layer for SWAPI interaction
- **Configuration Management**: Environment-specific configurations with profiles

#### Frontend Structure  
- **Feature-First Organization**: Modules organized by business domain
- **Shared Components**: Reusable UI components with consistent styling
- **Core Services**: Singleton services for application-wide functionality
- **Environment Configuration**: Build-time configuration for different environments

## 🔧 Troubleshooting

### Common Issues and Solutions

#### Port Conflicts
**Issue**: `Port 6969 or 8080 already in use`
**Solution**:
```bash
# Check what's using the ports
netstat -an | grep -E "(6969|8080)"
# Kill processes or modify docker-compose.yml ports
```

#### Memory Issues
**Issue**: `Container exits with OOMKilled`
**Solution**:
- Increase Docker Desktop memory to at least 4GB
- Check available system memory: `docker system df`
- Prune unused containers: `docker system prune -a`

#### Health Check Failures
**Issue**: `Backend health check failing`
**Solution**:
```bash
# Check backend logs
docker-compose logs backend
# Verify Spring Boot startup
curl http://localhost:8080/actuator/health
# Wait for full initialization (up to 2 minutes)
```

#### API Connection Issues
**Issue**: `Frontend cannot connect to backend`
**Solution**:
1. Verify backend is running: `docker-compose ps`
2. Check nginx proxy configuration in container
3. Verify environment variables are set correctly
4. Test direct API access: `curl http://localhost:8080/api/people`

### Development Tips

#### Debugging Backend
```bash
# Attach to running container
docker-compose exec backend bash
# View real-time logs with filtering
docker-compose logs -f backend | grep ERROR
# Check JVM memory usage
docker-compose exec backend jstat -gc 1
```

#### Debugging Frontend
```bash
# Check nginx configuration
docker-compose exec frontend cat /etc/nginx/conf.d/default.conf
# View access logs
docker-compose logs frontend | grep "GET /api"
# Test proxy configuration
docker-compose exec frontend curl -I http://backend:8080/actuator/health
```

#### Performance Monitoring
- **Backend Metrics**: Available at `/actuator/metrics`
- **Frontend Performance**: Use browser DevTools Performance tab
- **Database Queries**: Enable SQL logging in `application.properties`
- **Cache Statistics**: Available at `/actuator/caches`

### Log Analysis

#### Important Log Patterns
```bash
# Backend startup completion
grep "Started BackEndApplication" backend.log

# API call tracking  
grep "Processing request" backend.log

# Cache hit/miss statistics
grep "Cache" backend.log

# Frontend proxy requests
grep "proxy_pass" frontend.log
```

## 🤝 Contributing

I welcome contributions to improve this project. Here's how you can help:

### Development Workflow

1. **Fork the repository**
   ```bash
   git clone https://github.com/your-username/StarWarsWebApplication.git
   cd StarWarsWebApplication
   ```

2. **Create a feature branch**
   ```bash
   git checkout -b feature/your-feature-name
   ```

3. **Make your changes**
   - Follow existing code style and conventions
   - Add tests for new functionality
   - Update documentation as needed

4. **Test your changes**
   ```bash
   # Run all tests
   docker-compose run backend mvn test
   docker-compose run frontend npm test
   
   # Test the full application
   docker-compose up -d --build
   ```

5. **Submit a pull request**
   - Provide a clear description of changes
   - Reference any related issues
   - Ensure all CI checks pass

### Code Standards

- **Backend**: Follow Spring Boot best practices and Google Java Style Guide
- **Frontend**: Follow Angular style guide and use ESLint/Prettier
- **Testing**: Maintain test coverage above 80%
- **Documentation**: Update README and inline documentation
- **Commits**: Use conventional commit messages

## 📋 Requirements Compliance

This project fully satisfies the Full-Stack Developer Quiz requirements:

### ✅ Functional Requirements
- [x] **SWAPI Integration**: Displays data from Star Wars API
- [x] **Dual Tables**: Separate interfaces for People and Planets
- [x] **Pagination**: 15 items per page with proper navigation
- [x] **Search Functionality**: Case-insensitive partial name matching
- [x] **Sorting Capability**: Multiple criteria with ascending/descending order
- [x] **Open-Closed Principle**: Strategy pattern implementation for sorting
- [x] **Framework Requirements**: Angular frontend, Spring Boot backend
- [x] **Docker Deployment**: Complete containerization on port 6969

### ✅ Technical Requirements  
- [x] **Microservices Architecture**: Separate frontend and backend containers  
- [x] **Clean Code**: Comprehensive documentation and best practices
- [x] **Software Engineering Principles**: SOLID principles implementation
- [x] **Integration Tests**: Full test suite with 30+ backend tests
- [x] **Production Ready**: CI/CD pipeline with cloud deployment
- [x] **Performance Optimization**: Caching, lazy loading, and optimization
- [x] **UX/UI Design**: Modern Material Design with responsive layout

### ✅ Deliverables
- [x] **Source Code**: Complete application with clear structure
- [x] **README Documentation**: Comprehensive setup and usage guide  
- [x] **Docker Compose**: One-command deployment setup
- [x] **Live Demo**: Fully functional cloud deployment
- [x] **CI/CD Pipeline**: Automated testing and deployment

## 📄 License

This project is developed for educational and demonstration purposes as part of a technical assessment. Feel free to use it as a reference for similar projects.

---

**Built with ❤️ for the Star Wars community and modern web development practices.**
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
**Expected Results**: 30 tests passing including unit tests for controllers, services, and sorting strategies.

#### Frontend Tests
```bash
cd frontend
npm test
```
**Expected Results**: 7 tests passing with 35%+ code coverage for components and services.

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

## Railway Deployment

### Quick Railway Setup

1. **Import via Docker Compose** (Recommended):
   ```bash
   # Upload railway-compose.yml to Railway dashboard
   # Railway will automatically create both services
   ```

2. **Manual Service Creation**:
   - Create two services in Railway: `starwars-backend` and `starwars-frontend`
   - Set Root Directories: `BackEnd/` and `frontend/` respectively
   - Railway will auto-detect Dockerfiles in each directory

### Railway Configuration

**Backend Service**:
- Root Directory: `BackEnd/`
- Port: 8080 (auto-detected)
- Environment: `SPRING_PROFILES_ACTIVE=production`
- Private URL: `starwars-backend.railway.internal:8080`
- Public URL: `https://starwars-backend-production.up.railway.app`

**Frontend Service**:
- Root Directory: `frontend/`
- Port: 80 (auto-detected)  
- Environment: `BACKEND_URL=http://starwars-backend.railway.internal:8080`
- Public URL: `https://starwars-frontend-production.up.railway.app`

### Railway Features Used

- **Private Networking**: Secure internal communication between services
- **Automatic SSL**: HTTPS certificates for public URLs
- **Health Checks**: Built-in monitoring and restart policies
- **Zero Downtime**: Rolling deployments with health validation
- **Auto Scaling**: Automatic scaling based on traffic

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

The project includes a complete CI/CD pipeline using GitHub Actions and Railway deployment:

### Continuous Integration
- **Automated testing** for both frontend and backend
- **Code quality checks** and linting
- **Security scanning** with Trivy
- **Docker image building** and caching

### Continuous Deployment
- **Automatic deployment** to Railway on develop branch
- **Container registry** using GitHub Container Registry
- **Health check validation** before deployment
- **Private networking** between services for optimal performance

### Pipeline Stages
1. **Test Stage**: Run all unit tests for both services (30 backend tests, 7 frontend tests)
2. **Build Stage**: Create optimized Docker images
3. **Security Stage**: Scan for vulnerabilities
4. **Deploy Stage**: Deploy to Railway with zero downtime

### Railway Deployment
The application uses Railway's advanced deployment features:

- **Private Networking**: Internal service communication via `starwars-backend.railway.internal:8080`
- **Public URLs**: External access via Railway-generated domains
- **Automatic SSL**: HTTPS certificates managed by Railway
- **Health Monitoring**: Built-in health checks and monitoring
- **Scaling**: Automatic scaling based on demand

### Deployment Options
1. **Docker Compose Import**: Upload `railway-compose.yml` to Railway dashboard
2. **Individual Services**: Deploy backend and frontend as separate Railway services
3. **GitHub Integration**: Automatic deployment on code push

### Environments
- **Production**: `develop` branch → Railway Production
- **Local Development**: Docker Compose for local testing

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Run tests locally
5. Submit a pull request

## License

This project is for educational purposes.
# Star Wars Web Application

I built this full-stack playground to demo my ability to design, code and containerise a small product.  
The app pulls data from the public **[SWAPI](https://swapi.dev/)** and lets me browse, search and sort *People* and *Planets*.  
Everything ships via Docker Compose on port **6969** so reviewers can spin it up in under a minute.

---

## Table of contents
1. [Architecture](#architecture)  
2. [Project layout](#project-layout)  
3. [Requirements](#requirements)  
4. [Run with Docker Compose](#run-with-docker-compose)  
5. [Run locally without containers](#run-locally-without-containers)  
6. [Environment variables](#environment-variables)  
7. [Backend endpoints](#backend-endpoints)  
8. [Tests](#tests)  
9. [Handy scripts](#handy-scripts)  
10. [How to contribute](#how-to-contribute)  
11. [License](#license)

---

## Architecture
```
┌────────────┐        HTTP       ┌──────────────┐
│  Frontend  │ ───────────────► │   Backend    │
│ (Angular)  │                  │ (SpringBoot) │
└────────────┘  fetch /people   └──────────────┘
        ▲                               │
        └──────────── SWAPI ────────────┘
```
* **Frontend** – Angular 14 bundled into static files and served by Nginx.  
* **Backend**  – Spring Boot 3 exposing a REST API on `:8080`; I implemented pagination, filtering and open-closed sorting.  
* **Infra**     – Docker Compose wires both services together on a private network.

---

## Project layout
```
StarWarsWebApplication/
│  docker-compose.yml
│  README.md
├─ backend/
│   ├─ Dockerfile
│   └─ src/main/java/…
└─ frontend/
    ├─ Dockerfile
    └─ src/app/…
```

---

## Requirements
* Docker ≥ 20.10  
* Docker Compose v2  
* (Optional) JDK 17 and Node 18 if you prefer running without containers.

---

## Run with Docker Compose
```bash
# 1. clone
git clone https://github.com/<your-user>/StarWarsWebApplication.git
cd StarWarsWebApplication

# 2. build and start
docker compose up -d --build
```
Open `http://localhost:6969` and explore the UI.

### Check that everything is healthy
```bash
docker compose ps
curl http://localhost:8080/actuator/health   # backend health probe
```

### Stop the stack
```bash
docker compose down
```

---

## Run locally without containers
1. Backend  
   ```bash
   cd backend
   ./mvnw spring-boot:run
   ```  
2. Frontend  
   ```bash
   cd frontend
   npm install
   ng serve          # will be available at http://localhost:4200
   ```
Make sure `environment.ts` points to `http://localhost:8080`.

---

## Environment variables
| Variable      | Service  | What it does                                      | Default                |
|---------------|----------|---------------------------------------------------|------------------------|
| `API_URL`     | frontend | Where the UI should call the backend              | http://backend:8080    |
| `SERVER_PORT` | backend  | Internal port exposed by Spring Boot              | 8080                   |

---

## Backend endpoints
| Verb | Path             | Purpose                                | Query params                    |
|------|------------------|----------------------------------------|---------------------------------|
| GET  | `/api/people`    | Paged list of people                   | `page`, `size`, `search`, `sort`|
| GET  | `/api/planets`   | Paged list of planets                  | same as above                   |
| GET  | `/actuator/health` | Health-check for orchestrators       | —                               |

Example:  
`/api/people?page=0&size=15&search=sky&sort=name,asc`

---

## Tests
```bash
# backend
cd backend && ./mvnw test

# frontend
cd frontend && npm test
```
Integration tests must stay green in CI – they have saved me from shipping regressions more than once.

---

## Handy scripts
```bash
# wipe dangling images
docker image prune -f

# rebuild only the frontend image
docker compose build frontend
```

---

## How to contribute
1. Fork, then branch off something descriptive (`feature/new-table-sorting`).  
2. Follow Conventional Commits – it keeps the history tidy.  
3. Open a PR with context and screenshots.  
4. Please keep code coverage above 90 %.

---

## License
Released under the MIT license – see the [LICENSE](LICENSE) file for the legal bits.
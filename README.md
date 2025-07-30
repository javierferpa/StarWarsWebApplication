# Star Wars Web Application

This project is a full-stack web application that displays information about the Star Wars universe by consuming the [SWAPI (Star Wars API)](https://swapi.dev/). It is built as a microservices-oriented architecture with a separate frontend and backend, both containerized with Docker.

## Features

- **Two Separate Views**: Browse detailed, paginated tables for both **People** and **Planets**.
- **Server-Side Pagination**: Each table displays 15 items per page, with all data handling done on the backend to ensure performance.
- **Dynamic Search**: A real-time search bar allows filtering results by name for each category.
- **Robust Sorting**: Sort data by multiple columns, including `name` and `created` date, in both ascending and descending order.
- **Modern UI/UX**: A sleek, responsive, and Star Wars-themed interface built with Angular and Angular Material.
- **Containerized**: The entire application is orchestrated with Docker Compose, making setup and deployment incredibly simple.

## Tech Stack

- **Backend**: Java 21, Spring Boot 3
- **Frontend**: Angular 17, TypeScript, Angular Material
- **Containerization**: Docker & Docker Compose

## Getting Started

### Prerequisites

- [Docker](https://www.docker.com/products/docker-desktop/) must be installed and running on your system.
- Git (for cloning the repository).

### Running the Application

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/javierferpa/StarWarsWebApplication.git
    cd StarWarsWebApplication
    ```

2.  **Build and run the containers using Docker Compose:**
    Execute the following command from the root of the project. It will build the images for both the backend and frontend services and start them in detached mode.

    ```bash
    docker compose up -d --build
    ```

3.  **Access the application:**
    Once the containers are up and running, open your web browser and navigate to:
    [http://localhost:6969](http://localhost:6969)

    The frontend application will be served, which communicates with the backend API running on port 8080 (internally).

### Stopping the Application

To stop and remove the containers, run:
```bash
docker compose down
```

## Project Structure

The repository is organized into two main directories:

- `backend/`: Contains the Spring Boot application that acts as a proxy to the SWAPI. It handles all business logic, including fetching, searching, and sorting data.
- `frontend/`: Contains the Angular application that provides the user interface.

## Architectural Decisions

### Backend

The backend is designed following SOLID principles. A key feature is the **SorterFactory**, which implements the Open/Closed Principle. This factory dynamically selects the appropriate sorting strategy for a given entity (`Person` or `Planet`). This means new sorting logic or even new entities can be added with minimal changes to existing code, simply by providing a new `Sorter` implementation. This approach keeps the service layer clean and focused on its primary responsibilities.

### Frontend

The frontend is built with a modular approach. Reusable components like `DataTableComponent` and `SearchBarComponent` are used to maintain a consistent and maintainable codebase. The application state is managed within the respective list components (`people-list` and `planet-list`), which hold the query parameters for pagination, sorting, and searching.

## API Endpoints

The backend exposes the following endpoints, which are consumed by the frontend:

- `GET /api/people?page={p}&size={s}&sort={field},{dir}&search={term}`
- `GET /api/planets?page={p}&size={s}&sort={field},{dir}&search={term}`
- `GET /actuator/health`: A health check endpoint for monitoring.
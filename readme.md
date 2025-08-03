# CMMS Lite 🚧

A lightweight Computerized Maintenance Management System (CMMS) designed to streamline maintenance operations. The backend is built with Spring Boot, utilizing Spring Data JPA, Spring Security, and JWT for authentication. The frontend is developed using Angular with Tailwind CSS for styling. The application is containerized and orchestrated using Docker for easy deployment and scalability.

## ✨ Features

- 🛠 **Maintenance Management**: Efficiently manage assets, maintenance schedules, and work orders
- 📖 **API Documentation**: Automatically generated API documentation with SpringDoc OpenAPI
- 🔒 **Secure Authentication**: Implements JWT-based authentication with Spring Security
- 📱 **Responsive UI**: Built with Angular and styled using Tailwind CSS for a modern, responsive interface
- 🗄 **Database Support**: Supports both H2 (for development) and PostgreSQL (for production) databases
- 🐳 **Containerized Deployment**: Uses Docker and Docker Compose for simplified setup and deployment

## 🛠 Prerequisites

To run this project, ensure you have the following installed:

- ☕ **Java 17** or later (for the backend)
- 🌐 **Node.js 18.x** or later (for the Angular frontend)
- 🐳 **Docker** and **Docker Compose** (for containerized deployment)
- 🛠 **Maven** (for building the Spring Boot backend)
- 📂 **Git** (for cloning the repository)

## 🚀 Getting Started

### 📥 Clone the Repository

```bash
git clone https://github.com/your-username/cmms-lite.git
cd cmms-lite
```

## 🐳 Running with Docker

1. Ensure Docker and Docker Compose are installed
2. Build and run the application using Docker Compose:

```bash
docker-compose up --build
```

3. Access the application:
   - 🌐 **Frontend**: http://localhost:4200
   - 🔗 **Backend API**: http://localhost:8080
   - 📄 **API Documentation**: http://localhost:8080/swagger-ui-custom.html

## 🖥 Running Without Docker

### Backend

1. Navigate to the backend directory:
```bash
cd backend
```

2. Build and run the Spring Boot application:
```bash
./mvnw spring-boot:run
```

### Frontend

1. Navigate to the frontend directory:
```bash
cd frontend
```

2. Install dependencies:
```bash
npm install
```

3. Start the Angular development server:
```bash
ng serve
```

4. Access the frontend at http://localhost:4200

## 🐳 Docker Setup

The project includes a `docker-compose.yml` file that defines services for the backend, frontend, and PostgreSQL database.

### Docker Compose Configuration

- 🔧 **Backend**: Spring Boot application running on port 8080
- 🌐 **Frontend**: Angular application running on port 4200
- 🗄 **Database**: PostgreSQL database with persistent storage

To customize the Docker setup, modify the `docker-compose.yml` file or the respective Dockerfile in the backend and frontend directories.

## ⚙️ Configuration

### Backend

- 📝 **Application Properties**: Configure database settings, JWT secret, and other properties in `backend/src/main/resources/application.yml`
- 🗄 **Database**: By default, the application uses PostgreSQL in production and H2 in development. Update the database configuration in `application.yml` as needed

### Frontend

- 🌍 **Environment Variables**: Configure API endpoints and other settings in:
  - `frontend/src/environments/environment.ts` (for development)
  - `frontend/src/environments/environment.prod.ts` (for production)
- 🎨 **Tailwind CSS**: Tailwind configuration is located in `frontend/tailwind.config.js`

## 📦 Dependencies

### Backend

- ☕ Spring Boot 3.5.4
- 🗄 Spring Data JPA
- 🔒 Spring Security
- 🗄 PostgreSQL and H2 databases
- 🔑 JWT (io.jsonwebtoken:jjwt-api, jjwt-impl, jjwt-jackson v0.12.6)
- 🛠 Lombok
- 🔄 MapStruct 1.6.3
- 📖 SpringDoc OpenAPI 2.8.9
- ✅ Javax Validation 2.0.1

### Frontend

- 🌐 Angular 20.x
- 🎨 Tailwind CSS 4.x
- 🌍 Node.js 18.x or later

## 🏗 Building for Production

### Backend

```bash
cd backend
./mvnw clean package
```

The executable JAR will be generated in `backend/target`.

### Frontend

```bash
cd frontend
ng build --prod
```

The production-ready files will be generated in `frontend/dist`.

### Docker Production Build

Use Docker Compose to deploy the production build:

```bash
docker-compose -f docker-compose.prod.yml up --build
```

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. 🍴 Fork the repository
2. 🌿 Create a new branch (`git checkout -b feature/your-feature`)
3. 💾 Commit your changes (`git commit -m "Add your feature"`)
4. 🚀 Push to the branch (`git push origin feature/your-feature`)
5. 📬 Create a pull request

## 📜 License

This project is licensed under the MIT License. See the LICENSE file for details.
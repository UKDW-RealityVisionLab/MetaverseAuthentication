# MetaverseAuthentication
Auth service Backend for metaverse project

## Table of Contents
- [Features](#features)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Configuration](#configuration)
- [Usage](#usage)
- [License](#license)
- [Acknowledgments & Credits](#acknowledgments--credits)

## Features
- **Spring Boot Application**: Quickly set up and run RESTful services.
- **JPA & Hibernate**: Object Relational Mapping (ORM) for database interaction.
- **Security with Spring Security & JWT**: Secure endpoints using JSON Web Tokens (JWT).
- **Validation**: Input validation using `spring-boot-starter-validation`.
- **Lombok for Boilerplate Code**: Reduce boilerplate code like getters/setters.
- **Caching**: Integrated caching with `Ehcache`.
- **Google Integration**: Use of Google API for external services. (removed)
- **H2 Database for Testing**: An in-memory H2 database for development and testing purposes.

## Prerequisites
- **Java 17**: Ensure that you have JDK 17 installed on your machine.
- **Maven**: Apache Maven is required to manage dependencies and build the project.
- **MySQL / MariaDB** (Optional): If you intend to connect to a MySQL/MariaDB database, ensure it is installed and configured.
- **Google OAuth Client ID** (Optional): For integrating Google services.

## Installation
1. **Clone the Repository**:
    ```bash
    git clone https://github.com/UKDW-RealityVisionLab/MetaverseAuthentication.git
    cd MetaverseAuthentication
    ```
   
2. **Install Dependencies**:
   Use Maven to install project dependencies.
   ```bash
   mvn clean install

## Configuration
1. **Application Properties**: Configure the necessary properties in src/main/resources/application.yml.
   ```properties
   spring:
      datasource:      
         username: your mysql username
         password: your mysql password
      app:      
         oauth2:
            googleClientId: your-google-client-id
            googleSecret: your-google-secret

## Usage
1. **Development**: To start the development server, run:
   ```bash
   mvn spring-boot:run

2. **Create Package**: To Build the apps, run:
   ```bash
   mvn package

3. **Deploy on Docker**: To start the development server, run. Make sure Docker already installed:
   ```bash
   docker compose up -d dutaverse-auth

## Acknowledgments & credits
- **Spring Boot**: For creating easy-to-build and scalable backend systems.
- **Spring Security**: For powerful and highly customizable authentication and access-control framework.
- **Spring Data JPA**: To easily implement JPA-based (Java Persistence API) repositories
- **Hibernate**: For ORM and data persistence.
- **Project Lombok**: To simplify Java code with annotations.
- **H2 Databas**e: For easy in-memory database testing.
- **Google APIs**: For integration with Google services. 
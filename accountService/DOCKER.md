# Account Service - Docker Documentation

This document describes the containerization setup, build instructions, and best practices applied to the Docker environment for **accountService**.

---

## 📋 Prerequisites

To build and run this service, make sure you have the following installed on your machine:
* **Java 21**
* **Maven** (configured in your PATH)
* **Docker Engine** (running locally)

---

## 🚀 Build and Run Instructions

Follow these steps to package and run the application:

### Step 1: Package the Java Application
Run the Maven package command locally on your host machine to compile and generate the executable JAR file:
```bash
mvn clean package -DskipTests
```
This generates the packaged jar `target/accountService-0.0.1-SNAPSHOT.jar`.

### Step 2: Build the Docker Image
Build the Docker image using the optimized Dockerfile:
```bash
docker build -t account-service:latest -t account-service:1.0.0 .
```
* `-t account-service:latest`: Tags the build as `latest`.
* `-t account-service:1.0.0`: Tags the build with version `1.0.0` for version control.
* `.`: Targets the current directory context.

### Step 3: Run the Docker Container
Launch the container in detached background mode with port forwarding:
```bash
docker run -d -p 8080:8080 --name account-service account-service:latest
```

### Step 4: Verify the Application
* **Logs:** Check the startup logs of the Spring Boot application:
  ```bash
  docker logs -f account-service
  ```
* **Endpoints:**
  * Application homepage/APIs: `http://localhost:8080`
  * Spring Boot Actuator health checks: `http://localhost:8080/actuator`
  * H2 Console (in-memory DB): `http://localhost:8080/h2-console`

---

## 🛡️ Applied Docker Best Practices

The configuration uses several production-ready containerization guidelines:

### 1. Minimalistic Runtime (JLink Custom JRE)
Instead of shipping a full 300MB+ standard JRE, we use a multi-stage compilation that runs the JDK `jlink` utility to build a stripped-down Java runtime. The image only contains JVM modules strictly required by the Spring Boot web app (e.g., `java.base`, `java.sql`, `java.naming`, `java.instrument`, `jdk.unsupported`).
* **Benefit:** Reduced final image footprint from **524MB to 231MB** (~55% savings) and decreased container boot times.

### 2. Layer Size Optimization
The standard approach of copying a file followed by a `RUN chown` command duplicates the file size across Docker layers. 
Instead, we use:
```dockerfile
COPY --chown=spring:spring target/accountService-*.jar app.jar
```
* **Benefit:** Ownership is set during the copy process, avoiding extra intermediate filesystem layers.

### 3. Least Privilege Security (Non-Root User)
The container starts and executes processes using a non-root system user and group named `spring:spring` (UID/GID `1001`).
* **Benefit:** Prevents potential host-level exploitation if the containerized process is compromised.

### 4. Container-Aware JVM Settings
We pass standard container tuning options directly to the JVM:
```env
JAVA_TOOL_OPTIONS="-XX:+UseG1GC -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"
```
* **Benefit:** Ensures Java respects Docker memory limits to prevent out-of-memory (OOM) kills.

---

## 🛠️ Handy Docker Cheatsheet

| Task | Command |
|---|---|
| **Stop container** | `docker stop account-service` |
| **Start stopped container** | `docker start account-service` |
| **Remove container** | `docker rm account-service` |
| **Force remove running container** | `docker rm -f account-service` |
| **List images** | `docker images \| grep account-service` |
| **Shell access (diagnostics)** | `docker exec -it account-service /bin/sh` |

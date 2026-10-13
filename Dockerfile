# Stage 1: Build the application
FROM eclipse-temurin:25-jdk-noble AS build
WORKDIR /app

# Copy Maven wrapper and pom.xml first for efficient dependency caching
COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

# Copy application source code and package jar
COPY src ./src
RUN ./mvnw clean package -DskipTests

# Stage 2: Lightweight runtime image
FROM eclipse-temurin:25-jre-noble
WORKDIR /app

# Create a non-root group and user for security
RUN groupadd -r devpulse && useradd -r -g devpulse devpulse
USER devpulse

# Copy the built jar from build stage
COPY --from=build /app/target/devpulse-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

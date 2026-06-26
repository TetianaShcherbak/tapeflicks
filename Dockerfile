# --- Build stage ---
FROM gradle:8.10.2-jdk21-alpine AS build
WORKDIR /app

COPY build.gradle.kts settings.gradle.kts gradle.properties ./
COPY gradle ./gradle
RUN gradle dependencies --no-daemon || true

COPY src ./src
RUN gradle bootJar --no-daemon -x test

# --- Run stage ---
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Run as a non-root user - basic container hardening.
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

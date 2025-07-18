# Stage 1: Build the application
FROM maven:3.9-eclipse-temurin-17 AS builder

# Set build arguments
ARG MAVEN_OPTS="-Xmx1024m -XX:MaxPermSize=128m"
ARG SKIP_TESTS=true

WORKDIR /app

# Copy pom.xml and resolve dependencies (for better layer caching)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests=${SKIP_TESTS} -B

# Stage 2: Create the runtime image
FROM eclipse-temurin:17-jre-alpine

# Install dumb-init for proper signal handling
RUN apk add --no-cache dumb-init

# Set environment variables
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:+UnlockExperimentalVMOptions -XX:+UseZGC"
ENV SPRING_PROFILES_ACTIVE=production

WORKDIR /app

# Create logs directory
RUN mkdir -p /app/logs

# Add non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring

# Copy the built artifact from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Change ownership to spring user
RUN chown -R spring:spring /app

# Switch to non-root user
USER spring:spring

# Expose the application port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/api/actuator/health || exit 1

# Set the startup command with proper signal handling
ENTRYPOINT ["dumb-init", "--"]
CMD ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]

# Add labels for better maintainability
LABEL maintainer="AI Shreds Team"
LABEL version="1.0.0"
LABEL description="Component Reservation Management Shred"
LABEL component="component-reservation-shred"
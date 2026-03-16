# syntax=docker/dockerfile:1.7
#
# Production image for the Spring Boot monolith.
# - Multi-stage build keeps the runtime image small.
# - Maven deps are cached by copying pom+wrapper first.
# - No secrets are baked into the image; use env vars at runtime.
#

############################
# Build stage
############################
FROM eclipse-temurin:21-jdk-jammy AS build

WORKDIR /workspace

# Copy Maven wrapper and build descriptors first to maximize dependency caching.
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Some platforms don't preserve the executable bit for mvnw in git checkouts.
RUN chmod +x mvnw

# Pre-fetch dependencies (cache-friendly).
RUN ./mvnw -q -DskipTests dependency:go-offline

# Now copy sources and build the runnable jar.
COPY src/ src/
RUN ./mvnw -q -DskipTests package

############################
# Runtime stage
############################
FROM eclipse-temurin:21-jre-jammy AS runtime

# Run as non-root.
RUN useradd --create-home --shell /usr/sbin/nologin appuser
WORKDIR /app

# Copy the fat jar produced by Spring Boot plugin.
COPY --from=build /workspace/target/*.jar /app/app.jar

USER appuser

# The app is configured to listen on 8090 by default (see application.properties).
EXPOSE 8090

# Optional: default JVM tuning for containers; override via JAVA_TOOL_OPTIONS if desired.
ENV JAVA_TOOL_OPTIONS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75"

# Spring Boot reads configuration from environment variables automatically.
ENTRYPOINT ["java","-jar","/app/app.jar"]

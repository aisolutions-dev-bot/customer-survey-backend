# ---- Build Stage ----
FROM gradle:9.1.0-jdk25 AS build

WORKDIR /app

# GitHub credentials required for resolving ai-solutions-java-shared from GitHub Packages
# Set GITHUB_ACTOR and GITHUB_TOKEN in Railway Dashboard → Build Secrets
ARG GITHUB_ACTOR
ARG GITHUB_TOKEN

# Copy build config first (layer caching)
COPY gradlew ./
COPY gradle/ gradle/
COPY build.gradle settings.gradle ./

# Make Gradle wrapper executable
RUN chmod +x ./gradlew

# Pre-fetch dependencies with GitHub Packages auth
RUN GITHUB_ACTOR=$GITHUB_ACTOR GITHUB_TOKEN=$GITHUB_TOKEN \
  ./gradlew dependencies --no-daemon || true

# Copy full source
COPY . .

# Ensure gradlew is still executable after full copy
RUN chmod +x gradlew

# Build Spring Boot fat JAR with GitHub Packages auth
RUN GITHUB_ACTOR=$GITHUB_ACTOR GITHUB_TOKEN=$GITHUB_TOKEN \
  ./gradlew clean bootJar -x test -x check --no-daemon

# ---- Run Stage ----
FROM eclipse-temurin:25-jre-jammy

WORKDIR /app

# Copy built JAR from build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Railway passes $PORT — make sure app binds correctly
ENV PORT=8080
EXPOSE 8080

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]

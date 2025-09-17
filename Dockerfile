# ---- Build Stage ----
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

# Copy Gradle wrapper + settings first (better layer caching)
COPY gradlew ./
COPY gradle gradle
COPY build.gradle settings.gradle ./

# Give permission to gradlew
RUN chmod +x gradlew

# Download dependencies (cached if build.gradle/settings.gradle unchanged)
RUN ./gradlew --version
RUN ./gradlew dependencies --no-daemon || true

# Copy full source
COPY . .

# Build Spring Boot fat JAR (skip tests to speed up)
RUN ./gradlew clean bootJar -x test -x check --no-daemon

# ---- Run Stage ----
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy built JAR from build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Railway passes $PORT — make sure app binds correctly
ENV PORT=8080
EXPOSE 8080

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]

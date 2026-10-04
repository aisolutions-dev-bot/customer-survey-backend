# Builds a JVM runner for the Railway staging service.
FROM gradle:9.3.0-jdk25 AS builder

WORKDIR /app

COPY gradlew ./gradlew
COPY gradle/ ./gradle/
COPY build.gradle settings.gradle ./
COPY config/ ./config/

ARG GITHUB_ACTOR
ARG GITHUB_TOKEN

RUN chmod +x ./gradlew && \
    GITHUB_ACTOR="$GITHUB_ACTOR" GITHUB_TOKEN="$GITHUB_TOKEN" \
    ./gradlew dependencies --no-daemon

COPY src/ src/

RUN GITHUB_ACTOR="$GITHUB_ACTOR" GITHUB_TOKEN="$GITHUB_TOKEN" \
    ./gradlew bootJar --no-daemon && \
    application_jar=$(find build/libs -maxdepth 1 -type f -name '*.jar' ! -name '*-plain.jar' -print -quit) && \
    test -n "$application_jar" && \
    cp "$application_jar" /app/application.jar

FROM eclipse-temurin:25-jre-jammy

WORKDIR /app
COPY --from=builder /app/application.jar /app/application.jar

ENV PORT=8090
EXPOSE 8090
ENTRYPOINT ["java", "-jar", "/app/application.jar"]

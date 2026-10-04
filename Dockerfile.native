# syntax=docker/dockerfile:1.10
FROM ghcr.io/graalvm/native-image-community:25 AS build

WORKDIR /app
ARG GITHUB_ACTOR

COPY gradlew ./gradlew
COPY gradle/ gradle/
COPY build.gradle settings.gradle ./
RUN chmod +x ./gradlew

COPY . .

RUN --mount=type=secret,id=github_token,env=GITHUB_TOKEN \
    --mount=type=cache,target=/root/.gradle \
    GITHUB_ACTOR="$GITHUB_ACTOR" \
    ./gradlew clean nativeCompile --no-daemon

FROM debian:bookworm-slim

RUN apt-get update \
    && apt-get install --no-install-recommends --yes ca-certificates libz1 \
    && rm -rf /var/lib/apt/lists/* \
    && useradd --no-create-home --shell /usr/sbin/nologin --uid 10001 appuser

WORKDIR /app
COPY --from=build /app/build/native/nativeCompile/customer-survey-backend /app/application

USER 10001
ENV PORT=8090
EXPOSE 8090
ENTRYPOINT ["/app/application"]

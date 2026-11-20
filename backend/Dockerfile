FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /workspace
COPY pom.xml ./
RUN mvn --batch-mode dependency:go-offline

COPY src ./src
RUN mvn --batch-mode clean package -DskipTests \
    && JAR_FILE=$(find /workspace/target -maxdepth 1 -type f -name "*.jar" ! -name "*.original" | head -n 1) \
    && test -n "$JAR_FILE" \
    && cp "$JAR_FILE" /workspace/target/app.jar

FROM eclipse-temurin:17-jre-jammy

RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/* \
    && groupadd --system appgroup \
    && useradd --system --gid appgroup --home-dir /app appuser

WORKDIR /app
COPY --from=build --chown=appuser:appgroup /workspace/target/app.jar /app/app.jar

USER appuser
EXPOSE 8080

HEALTHCHECK --interval=10s --timeout=3s --start-period=20s --retries=6 \
    CMD curl --fail --silent http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-XX:MaxRAMPercentage=70.0", "-jar", "/app/app.jar"]
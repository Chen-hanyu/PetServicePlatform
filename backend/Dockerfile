FROM maven:3.9.9-eclipse-temurin-17 AS builder
WORKDIR /build

COPY backend/pom.xml .
COPY backend/src src

RUN mvn -q -DskipTests package


FROM eclipse-temurin:17-jre
WORKDIR /app

# 创建非 root 用户
RUN groupadd -r appuser && useradd -r -g appuser -d /app -s /sbin/nologin appuser

COPY --from=builder /build/target/PetServicePlatform-0.0.1-SNAPSHOT.jar app.jar

# Create resource/images directory for static image assets
RUN mkdir -p /app/resource/images && chown -R appuser:appuser /app

# 健康检查
HEALTHCHECK --interval=30s --timeout=10s --retries=3 \
  CMD wget -qO- http://localhost:8080/health || exit 1

EXPOSE 8080

USER appuser

ENTRYPOINT ["java", "-jar", "/app/app.jar"]

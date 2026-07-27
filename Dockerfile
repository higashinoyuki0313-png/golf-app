# ===== ビルドステージ =====
FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace

# 依存解決をレイヤーキャッシュさせるため、まずビルド定義だけコピー
COPY gradlew settings.gradle build.gradle ./
COPY gradle ./gradle
RUN ./gradlew --no-daemon dependencies > /dev/null 2>&1 || true

COPY src ./src
RUN ./gradlew --no-daemon bootJar -x test

# ===== 実行ステージ =====
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /workspace/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

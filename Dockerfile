# --- Etapa de build ---
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Copiar wrapper y archivos de configuración primero para aprovechar la cache de capas
COPY gradlew settings.gradle build.gradle ./
COPY gradle ./gradle
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon || true

# Copiar el resto del código y construir el jar
COPY src ./src
RUN ./gradlew bootJar --no-daemon -x test

# --- Etapa de ejecución ---
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

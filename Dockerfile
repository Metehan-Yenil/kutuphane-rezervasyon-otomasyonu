# Multi-stage build için Maven ve JDK 25 kullanan Dockerfile

# Build stage
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

# pom.xml ve Maven wrapper dosyalarını kopyala
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Bağımlılıkları indir (cache için)
RUN mvn dependency:go-offline -B

# Kaynak kodları kopyala
COPY src ./src

# Uygulamayı derle (testleri atla)
RUN mvn clean package -DskipTests

# Runtime stage - JDK 21 (Java 25 resmi image henüz yok, 21 kullanıyoruz)
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# JAR dosyasını build stage'den kopyala
COPY --from=build /app/target/*.jar app.jar

# PostgreSQL bağlantısı için gerekli port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# JVM parametreleri ile uygulamayı başlat
ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", \
    "app.jar"]

# Giai đoạn 1: Build project bằng Maven
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline -B
COPY src src
RUN ./mvnw clean package -DskipTests

# Giai đoạn 2: Chạy ứng dụng với JRE nhẹ hơn
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-XX:TieredStopAtLevel=1", "-Xshare:auto", "-Djava.security.egd=file:/dev/./urandom", "-Xmx400m", "-jar", "app.jar"]
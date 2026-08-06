# 1. Mavenを使ってビルド
FROM maven:3.9-eclipse-temurin-21 AS build
COPY . .
RUN mvn clean package -DskipTests

# 2. 実行環境でアプリを起動
FROM eclipse-temurin:21-jre
COPY --from=build /target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
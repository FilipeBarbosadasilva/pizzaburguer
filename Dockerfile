
# Estágio 1: Compilar o backend Java
FROM maven:3.9-eclipse-temurin-21 AS backend-build

WORKDIR /app

COPY . .

RUN mvn -f backend/pom.xml -DskipTests clean package

# Estágio 2: Executar Spring Boot
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=backend-build /app/backend/target/*.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]

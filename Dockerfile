# FROM maven:3.9-eclipse-temurin-21 AS build
# WORKDIR /app
# COPY . .
# RUN mvn -f backend/pom.xml -DskipTests clean package

# FROM eclipse-temurin:21-jre
# WORKDIR /app
# COPY --from=build /app/backend/target/*.jar app.jar
# EXPOSE 8081
# CMD ["java", "-jar", "app.jar"]

# Estágio 1: Build do Frontend (Exemplo usando Node para Vite/React)
FROM node:20 AS frontend-build
WORKDIR /app/frontend
# Copia os arquivos do front e faz o build
COPY frontend/package*.json ./
RUN npm install
COPY frontend/ ./
RUN npm run build

# Estágio 2: Build do Backend (Java Maven)
FROM maven:3.9-eclipse-temurin-21 AS backend-build
WORKDIR /app
COPY . .
# Copia o resultado do build do frontend para a pasta de recursos estáticos do Spring Boot
COPY --from=frontend-build /app/frontend/dist /app/backend/src/main/resources/static
RUN mvn -f backend/pom.xml -DskipTests clean package

# Estágio 3: Execução da aplicação unificada
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=backend-build /app/backend/target/*.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]

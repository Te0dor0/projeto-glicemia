# ===== BUILD STAGE =====
FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /app

# Copia o pom.xml e as dependências primeiro
COPY backend/pom.xml .
RUN mvn dependency:go-offline -B

# Copia o código fonte e compila
COPY backend/src ./src
RUN mvn clean package -DskipTests

# ===== RUN STAGE =====
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Cria diretório para o Excel
RUN mkdir -p /app/data

# Copia o jar gerado
COPY --from=build /app/target/glicemia-backend-1.0.0.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

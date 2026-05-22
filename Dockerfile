# ===== BUILD STAGE =====
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

# Instala Maven
RUN apk add --no-cache maven

# Copia apenas o pom.xml primeiro para aproveitar o cache das dependências
COPY backend/pom.xml .
RUN mvn dependency:go-offline -B

# Copia o código fonte e compila
COPY backend/src ./src
RUN mvn clean package -DskipTests

# ===== RUN STAGE =====
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Cria diretório para o Excel se necessário
RUN mkdir -p /app/data

# Copia o jar gerado no estágio de build
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

# Comando para rodar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]

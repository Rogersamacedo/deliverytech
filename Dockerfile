# ---------- Estágio 1: build ----------
# Usa a imagem do Maven, então não depende do mvnw nem do .mvn/wrapper
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Baixa as dependências primeiro (aproveita o cache do Docker)
COPY pom.xml .
RUN mvn -q dependency:go-offline

# Copia o código e gera o JAR (testes rodam no CI, não aqui)
COPY src src
RUN mvn -q clean package -DskipTests

# ---------- Estágio 2: execução ----------
FROM eclipse-temurin:21-jre
WORKDIR /app

# O nome do JAR muda com a versão, por isso o curinga
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

# JWT_SECRET é obrigatório (mínimo de 32 caracteres) e vem do docker-compose
ENTRYPOINT ["java", "-jar", "app.jar"]

# Stage 1: Build con Maven
FROM maven:3.9-eclipse-temurin-21-alpine AS builder

# Establecer directorio de trabajo
WORKDIR /app

# Copiar archivos de configuración de Maven
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Descargar dependencias (esto se cachea si no cambia el pom.xml)
RUN mvn dependency:go-offline -B

# Copiar el código fuente
COPY src ./src

# Construir la aplicación (skip tests para build más rápido en producción)
RUN mvn clean package -DskipTests

# Stage 2: Runtime con JRE
FROM eclipse-temurin:21-jre-alpine

# Crear usuario no-root para seguridad
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Establecer directorio de trabajo
WORKDIR /app

# Copiar el JAR construido desde el stage anterior
COPY --from=builder /app/target/*.jar app.jar

# Exponer puerto 8080 (puerto por defecto de Spring Boot)
EXPOSE 8080

# Variables de entorno por defecto (pueden ser sobrescritas en Cloud Run)
ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS="-Xmx256m -Xms128m"

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Comando para ejecutar la aplicación
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

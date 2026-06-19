# ============================================================
#  Dockerfile del BACKEND (Spring Boot)
#  Un Dockerfile es una "receta" para empaquetar la app en una imagen
#  que se puede correr en cualquier servidor sin instalar Java a mano.
#  Usamos 2 etapas: una para compilar y otra (mas liviana) para ejecutar.
# ============================================================

# ---- Etapa 1: compilar el proyecto y generar el .jar ----
FROM eclipse-temurin:25-jdk AS build
WORKDIR /app

# Copiamos primero el wrapper de Maven y el pom para aprovechar la cache de Docker.
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw -q dependency:go-offline || true

# Ahora copiamos el codigo fuente y compilamos (sin correr los tests).
COPY src ./src
RUN ./mvnw -q -DskipTests clean package

# ---- Etapa 2: imagen final, liviana, solo para ejecutar ----
FROM eclipse-temurin:25-jre
WORKDIR /app

# Traemos el .jar generado en la etapa anterior.
COPY --from=build /app/target/*.jar app.jar

# El backend escucha en el puerto 8080.
EXPOSE 8080

# Comando que arranca la aplicacion.
ENTRYPOINT ["java", "-jar", "app.jar"]

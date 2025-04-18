FROM eclipse-temurin:17-jdk as build

WORKDIR /app

COPY . .

WORKDIR /app/auth-service
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY --from=build /app/auth-service/target/auth-service-*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]

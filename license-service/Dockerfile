FROM eclipse-temurin:17-jdk as build

WORKDIR /app

COPY . .

WORKDIR /app/license-service
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY --from=build /app/license-service/target/license-service-*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]

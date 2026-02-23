FROM eclipse-temurin:21-jdk-jammy AS build

WORKDIR /app

COPY pom.xml mvnw* ./
COPY .mvn .mvn
RUN ./mvnw dependency:go-offline -B

COPY src ./src

RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre-jammy

RUN useradd -m springuser
USER springuser

WORKDIR /app

COPY --from=build /app/target/*.jar transfer-service.jar

ENV JAVA_OPTS="-Xms256m -Xmx512m -Djava.security.egd=file:/dev/./urandom"

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar transfer-service.jar"]
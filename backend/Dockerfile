# Build stage
FROM gradle:8.4-jdk17 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
# Ensure gradlew is executable
RUN chmod +x gradlew
RUN ./gradlew build --no-daemon -x test

# Package stage
FROM openjdk:17-jdk-slim
EXPOSE 8080
# Copy the executable jar (ignoring the -plain.jar)
COPY --from=build /home/gradle/src/build/libs/*[!plain].jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]


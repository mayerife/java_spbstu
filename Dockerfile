FROM gradle:8.4-jdk17 AS build
WORKDIR /workspace

COPY build.gradle settings.gradle gradlew gradle/ ./

RUN gradle --no-daemon dependencies

COPY . .

RUN gradle --no-daemon clean bootJar -x test

FROM eclipse-temurin:17-jdk-jammy AS runtime
WORKDIR /app

COPY --from=build  /workspace/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
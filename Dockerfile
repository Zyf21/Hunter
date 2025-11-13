## stage 1: build
#FROM gradle:8-jdk17 AS build
#WORKDIR /home/gradle/project
#COPY --chown=gradle:gradle . .
## соберём jar (bootJar) без запуска тестов в этом шаге, тесты мы прогоняем отдельно в Jenkins
#RUN gradle bootJar --no-daemon
#
## stage 2: runtime
#FROM eclipse-temurin:17-jre
#WORKDIR /app
#COPY --from=build /home/gradle/project/build/libs/*.jar app.jar
#ENTRYPOINT ["java","-jar","/app/app.jar"]


# stage 1: build
FROM gradle:8-jdk17 AS build
WORKDIR /home/gradle/project

# Копируем только файлы gradle и зависимости, чтобы кешировать слои
COPY build.gradle settings.gradle gradlew ./
COPY gradle gradle/
RUN gradle --no-daemon dependencies || true

# Копируем остальные исходники
COPY . .
# Собираем jar без тестов
RUN ./gradlew bootJar --no-daemon -x test

# stage 2: runtime
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /home/gradle/project/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]


## stage 1: build
#FROM gradle:8-jdk17 AS build
#WORKDIR /home/gradle/project
#
## Копируем только файлы для зависимостей первыми
#COPY gradle/wrapper/gradle-wrapper.properties gradle/wrapper/
#COPY gradlew .
#COPY settings.gradle .
#COPY build.gradle .
#
## Скачиваем зависимости (кешируется если build.gradle/settings.gradle не менялись)
#RUN gradle --no-daemon dependencies --stacktrace || return 0
#
## Копируем исходный код
#COPY src src/
#
## Собираем jar
#RUN ./gradlew bootJar --no-daemon -x test --stacktrace
#
## stage 2: runtime
#FROM eclipse-temurin:17-jre
#WORKDIR /app
#COPY --from=build /home/gradle/project/build/libs/*.jar app.jar
#ENTRYPOINT ["java", "-jar", "/app/app.jar"]



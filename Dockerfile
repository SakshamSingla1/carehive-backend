FROM maven:3.8.5-openjdk-17 AS build
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:17.0.1-jdk-slim
# ✅ Install CA certificates so SSL with MongoDB Atlas works
RUN apt-get update && apt-get install -y ca-certificates && update-ca-certificates && rm -rf /var/lib/apt/lists/*

COPY --from=build /target/CareHive-0.0.1-SNAPSHOT.jar CareHive.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","CareHive.jar"]


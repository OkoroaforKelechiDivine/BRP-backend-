FROM eclipse-temurin:21.0.1_12-jdk-alpine AS builder
# create workdir
WORKDIR /opt/brp
# copy mvn
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
# go-offline using the pom.xml
RUN ./mvnw dependency:go-offline
#copy other important files
copy src ./src
# compile the source code and package it in a jar file
RUN ./mvnw package -Dmaven.test.skip=true
LABEL authors="divjazz"

# Stage 2
# Set base image for stage 2
FROM eclipse-temurin:21.0.1_12-jre-alpine AS final
WORKDIR /opt/brp
EXPOSE 9090
COPY --from=builder /opt/brp/target/*.jar /opt/brp/*.jar
ENTRYPOINT ["java", "-jar", "--enable-preview", "/opt/recommendic/*.jar"]
# Select a base image
FROM adoptopenjdk/openjdk13:alpine-jre

# Set working directory
WORKDIR /usr/app

# Copy the target jars into the working directory
COPY target/*.jar app.jar

# java -jar /opt/app/app.jar
ENTRYPOINT ["java","-jar","app.jar"]

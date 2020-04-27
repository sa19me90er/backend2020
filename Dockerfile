# Select a base image
FROM adoptopenjdk/openjdk13:alpine-jre
# Set working directory
WORKDIR /usr/app
# Copy target jars into the working directory
COPY target/*.jar app.jar
# Set the command to run upon start
ENTRYPOINT ["java","-jar","app.jar"]
# Use a base image with OpenJDK 8 and Maven
FROM maven:3.8.7-openjdk-8 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the project files into the container
COPY . .

# Run Maven to build the application
RUN mvn clean install

# Second stage: Use Tomcat to deploy the application
FROM tomcat:8.0

# Copy the built WAR file to the Tomcat webapps directory
COPY --from=build /app/target/shopping-cart.war /usr/local/tomcat/webapps/shopping-cart.war

# Expose the port the app will run on
EXPOSE 8080

# Set the command to run Tomcat
CMD ["catalina.sh", "run"]

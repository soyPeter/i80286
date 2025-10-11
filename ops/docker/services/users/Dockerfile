FROM eclipse-temurin:21-jre-jammy

# Create a non-root user
RUN addgroup --system appgroup && adduser --system --ingroup appgroup appuser

# Create application directory
WORKDIR /opt/bitnomio

# Copy the application
COPY services/users/build/libs/users-*.jar /opt/bitnomio/

# Expose the service port
EXPOSE $SERVICE_PORT

# Switch to the non-root user
USER appuser

# Set the entrypoint
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-XX:InitialRAMPercentage=50.0", "-XX:+UseG1GC", "-XX:+ExitOnOutOfMemoryError", "-Dfile.encoding=UTF-8", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/opt/bitnomio/users.jar"]

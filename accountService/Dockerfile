# ==========================================
# Stage 1: Build Custom Minimal JRE (jlink)
# ==========================================
FROM eclipse-temurin:21-jdk-alpine AS jre-builder

WORKDIR /jre

# Build custom minimal JRE containing only required modules for Spring Boot
RUN $JAVA_HOME/bin/jlink \
    --add-modules java.base,java.compiler,java.desktop,java.instrument,java.management,java.naming,java.prefs,java.rmi,java.scripting,java.security.jgss,java.security.sasl,java.sql,java.xml,jdk.unsupported \
    --strip-debug \
    --no-man-pages \
    --no-header-files \
    --compress=2 \
    --output /javaruntime

# ==========================================
# Stage 2: Final Runtime Stage
# ==========================================
FROM alpine:3.20.1

ENV JAVA_HOME=/opt/java/openjdk
ENV PATH="${JAVA_HOME}/bin:${PATH}"

# Copy the custom minimal JRE from the builder stage
COPY --from=jre-builder /javaruntime $JAVA_HOME

# Set useful default environment variables
ENV SPRING_OUTPUT_ANSI_ENABLED=ALWAYS \
    JAVA_TOOL_OPTIONS="-XX:+UseG1GC -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# Create a non-root system group and user for security (Least Privilege Principle)
RUN addgroup -g 1001 -S spring && \
    adduser -u 1001 -S spring -G spring

WORKDIR /app

# Copy the pre-built jar and set ownership in a single step to avoid layer duplication
COPY --chown=spring:spring target/accountService-*.jar app.jar

# Switch to the non-root user
USER spring

# Expose the port specified in application.yaml
EXPOSE 8080

# Execute the application directly (no shell wrapper needed)
ENTRYPOINT ["java", "-jar", "app.jar"]

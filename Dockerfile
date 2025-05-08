FROM eclipse-temurin:21-jre-jammy as builder
WORKDIR extracted
ADD ./build/libs/Misale-0.0.1-SNAPSHOT.jar app.jar
RUN java -Djarmode=layertools -jar app.jar extract

FROM eclipse-temurin:21-jre-jammy

# Install Redis
RUN apt-get update && \
    apt-get install -y redis-server && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Configure Redis to run without password and bind to all interfaces
RUN sed -i 's/bind 127.0.0.1 -::1/bind 0.0.0.0/g' /etc/redis/redis.conf && \
    sed -i 's/protected-mode yes/protected-mode no/g' /etc/redis/redis.conf && \
    sed -i 's/# requirepass foobared/# requirepass disabled/g' /etc/redis/redis.conf

WORKDIR application
COPY --from=builder extracted/dependencies/ ./
COPY --from=builder extracted/spring-boot-loader/ ./
COPY --from=builder extracted/snapshot-dependencies/ ./
COPY --from=builder extracted/application/ ./

# Add startup script to run both Redis and Spring Boot app
COPY start.sh /start.sh
RUN chmod +x /start.sh

EXPOSE 8080 6379

ENTRYPOINT ["/start.sh"]
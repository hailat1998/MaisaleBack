#!/bin/bash
# Start Redis server in the background
redis-server /etc/redis/redis.conf --daemonize yes

# Start Spring Boot application
exec java org.springframework.boot.loader.launch.JarLauncher

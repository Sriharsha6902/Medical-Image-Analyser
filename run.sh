#!/bin/bash
# Start the Spring Boot application in the background
mvn spring-boot:run &

# Start the uvicorn server in the foreground
uvicorn main:app --port 5000
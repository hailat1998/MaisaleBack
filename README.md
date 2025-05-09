# Amharic LLM Backend Service

![Amharic LLM](https://img.shields.io/badge/Amharic-LLM%20Service-blue)
![Spring WebFlux](https://img.shields.io/badge/Spring-WebFlux-green)
![Redis](https://img.shields.io/badge/Cache-Redis-red)
![Gemini AI](https://img.shields.io/badge/LLM-Gemini%20AI-purple)



## Overview

This backend service is a crucial component of an Android application designed to handle Amharic language processing through Large Language Models (LLMs). The service efficiently manages client requests, forwards them to the LLM, caches results for performance optimization, and delivers responses back to the client.

## API Endpoints

### Proverb Meaning Endpoints

These endpoints help users understand Amharic proverbs by providing translations and explanations:

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/meaning/en-meaning` | POST | Get English meaning of an Amharic proverb |
| `/meaning/am-meaning` | POST | Get Amharic explanation of an Amharic proverb |
| `/meaning` | POST | Get both English and Amharic meanings of an Amharic proverb |

**Example Request:**
```json
{
  "proverb": "ሀምሳ ሎሚ ለአንድ ሰው ሸክሙ ነው"
}
```


### Translation Endpoints
These endpoints provide translation services between Amharic and English:

| Endpoint | Method | Description |
|----------|--------|-------------|
|`/translate/en2am`	|POST	|Translate English text to Amharic
|`/translate/la2am`	|POST	|Convert Latin-written Amharic (Fidel) to proper Amharic script
|`/translate/enOrLa2am`	|POST	|Intelligently detect and translate English or Latin-written Amharic to Amharic

#### Example Request Body:

For English translation: 
```json
{
  "word": "love"
}
```
For Latin-to-Amharic: "Selam"
```json
{
  "word": "Selam"
}
```

## Key Technologies
### Spring WebFlux
The service leverages Spring WebFlux for asynchronous, non-blocking operations. This architecture was chosen to:

* Prevent thread exhaustion during frequent blocking calls
* Achieve higher throughput with minimal resource consumption
* Handle concurrent requests efficiently
### Redis Caching
**Redis** is implemented as a caching layer to:

* Eliminate redundant LLM queries for previously processed requests
* Significantly reduce response times for common queries
* Decrease API usage costs and resource consumption
* Improve overall application performance
### Langchain4J with Gemini AI
The service utilizes **Langchain4J** framework to interact with Google's Gemini AI model, selected for its:

* Comprehensive understanding of Amharic language
* Detailed and contextually accurate responses
* Strong performance on translation and cultural context tasks 

## Installation and Setup
  ### Prerequisites
  * Java 17 or higher
  * Gradle
  * Redis
  * Google AI Studio API key
  * Environment Configuration
  * Set up your Gemini API key:
```bash
  export GEMINI_AI_KEY=<YOUR_API_KEY>
  ```
### Redis Setup
Install and start Redis:
```bash
# Ubuntu/Debian
sudo apt install redis-server
sudo systemctl start redis

# macOS (using Homebrew)
brew install redis
brew services start redis
```

### Building and Running the Service
```bash
# Clone the repository
git clone https://github.com/hailat1998/Misale.git
cd Misale

# Build the application
./gradlew app:bootJar

# Run the application
java -jar app/build/libs/app-0.0.1-SNAPSHOT.jar
```

## Testing the API
You can test the API endpoints using the provided test script:
```bash
# Make the script executable
chmod +x test_all.sh

# Run the tests (replace with your server URL)
./test_all.sh http://localhost:8080
```
### This project is one part of [MisaleawiAnegager](https://github.com/hailat1998/MisaleawiAnegager)

Thank you for checking out this project! For questions or feedback, please open an issue on GitHub.
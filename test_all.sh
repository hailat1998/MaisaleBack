#!/bin/bash

# Check if base URL is provided as argument
if [ $# -ne 1 ]; then
    echo "Usage: $0 <base_url>"
    echo "Example: $0 http://localhost:8080"
    exit 1
fi

BASE_URL=$1
TIMEOUT=120  # 2 minutes timeout in seconds
CONTENT_TYPE="Content-Type: application/json"

echo "Testing API endpoints with base URL: $BASE_URL"
echo "Timeout set to $TIMEOUT seconds"
echo "----------------------------------------"

# Test /meaning/en-meaning endpoint
echo "Testing /meaning/en-meaning endpoint..."
curl -s -X POST \
    -H "$CONTENT_TYPE" \
    -d '{"proverb": "ሀምሳ ሎሚ ለአንድ ሰው ሸክሙ ነው"}' \
    --max-time $TIMEOUT \
    "$BASE_URL/meaning/en-meaning"
echo -e "\n----------------------------------------"

# Test /meaning/am-meaning endpoint
echo "Testing /meaning/am-meaning endpoint..."
curl -s -X POST \
    -H "$CONTENT_TYPE" \
    -d '{"proverb": "ሀብታም በገንዘቡ ድሃ በጉልበቱ"}' \
    --max-time $TIMEOUT \
    "$BASE_URL/meaning/am-meaning"
echo -e "\n----------------------------------------"

# Test /meaning endpoint
echo "Testing /meaning endpoint..."
curl -s -X POST \
    -H "$CONTENT_TYPE" \
    -d '{"proverb": "ሀምሳ ሎሚ ለአንድ ሰው ሸክሙ ነው"}' \
    --max-time $TIMEOUT \
    "$BASE_URL/meaning"
echo -e "\n----------------------------------------"

# Test /translate/la2am endpoint
echo "Testing /translate/la2am endpoint..."
curl -s -X POST \
    -H "$CONTENT_TYPE" \
    -d '{"text": "sew"}' \
    --max-time $TIMEOUT \
    "$BASE_URL/translate/la2am"
echo -e "\n----------------------------------------"

# Test /translate/en2am endpoint
echo "Testing /translate/en2am endpoint..."
curl -s -X POST \
    -H "$CONTENT_TYPE" \
    -d '{"text": "love"}' \
    --max-time $TIMEOUT \
    "$BASE_URL/translate/en2am"
echo -e "\n----------------------------------------"

# Test /translate/enOrLa2am endpoint
echo "Testing /translate/enOrLa2am endpoint..."
curl -s -X POST \
    -H "$CONTENT_TYPE" \
    -d '{"text": "Selam"}' \
    --max-time $TIMEOUT \
    "$BASE_URL/translate/enOrLa2am"
echo -e "\n----------------------------------------"

echo "All tests completed."

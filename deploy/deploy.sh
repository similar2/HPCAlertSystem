#!/bin/bash
set -e  # Exit immediately if a command exits with a non-zero status

# Define directories and paths
PROMETHEUS_DIR="./prometheus"
DATA_DIR="$PROMETHEUS_DIR/data"

echo "Starting deployment..."

# Step 1: Create the 'data' directory and set permissions
echo "Ensuring data directory exists and is writable..."
mkdir -p "$DATA_DIR"
chmod 777 "$DATA_DIR"
echo "Data directory prepared at $DATA_DIR"

# Step 2: Start Docker containers
echo "Starting Docker containers with docker-compose..."
docker compose up -d
echo "Docker containers are up and running."

echo "Deployment completed successfully!"

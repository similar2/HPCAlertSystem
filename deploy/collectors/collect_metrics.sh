#!/bin/bash
# collect_metrics.sh - Script to collect and write metrics in a loop

# Directory to write metrics
METRICS_DIR="./metrics"

# Ensure the metrics directory exists
mkdir -p "$METRICS_DIR"

# Function to collect logged-in users
collect_logged_in_users() {
    # Get unique logged-in users
    unique_user_count=$(who | awk '{print $1}' | sort | uniq | wc -l)

    # Write metric to the .prom file in Prometheus format
    metrics_file="$METRICS_DIR/logged_in_users.prom"
    touch "$metrics_file"

    # Overwrite with new data in Prometheus format
    echo "# HELP logged_in_users Number of unique logged-in users." > "$metrics_file"
    echo "# TYPE logged_in_users gauge" >> "$metrics_file"
    echo "logged_in_users $unique_user_count" >> "$metrics_file"
}

# Main loop - runs indefinitely, updating the metrics every 1 second (adjust as needed)
while true; do
    collect_logged_in_users
    sleep 1  # Sleep for 1 second before updating again
done

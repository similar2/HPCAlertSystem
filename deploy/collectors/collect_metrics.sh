#!/bin/bash
# collect_metrics.sh - Script to collect and write metrics in a loop

# Directory to write metrics
METRICS_DIR="./metrics"

# Ensure the metrics directory exists
mkdir -p "$METRICS_DIR"

# Function to collect logged-in users
collect_logged_in_users() {
    # Count logged-in users
    user_count=$(who | wc -l)

    touch "$METRICS_DIR/logged_in_users.prom"

    # Write metric to the .prom file in Prometheus format
    echo "# HELP logged_in_users Number of logged-in users." > "$METRICS_DIR/logged_in_users.prom"
    echo "# TYPE logged_in_users gauge" >> "$METRICS_DIR/logged_in_users.prom"
    echo "logged_in_users $user_count" >> "$METRICS_DIR/logged_in_users.prom"
}

# Main loop - this will run indefinitely, updating the metrics every 1 seconds (adjust as needed)
while true; do
    collect_logged_in_users
    sleep 1  # Sleep for 1 seconds before updating again
done
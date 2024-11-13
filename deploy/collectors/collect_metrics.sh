#!/bin/bash
# collect_metrics.sh - Script to collect and write metrics in a loop

# Directory to write metrics
METRICS_DIR="./metrics"

# Ensure the metrics directory exists
mkdir -p "$METRICS_DIR"

# Function to collect logged-in users and their names
collect_logged_in_users() {
    # Get unique logged-in users
    unique_user_count=$(who | awk '{print $1}' | sort | uniq | wc -l)
    unique_user_names=$(who | awk '{print $1}' | sort | uniq)

    # Write metric to the .prom file in Prometheus format
    metrics_file="$METRICS_DIR/logged_in_users.prom"
    touch "$metrics_file"

    # Overwrite with new data in Prometheus format
    echo "# HELP logged_in_users Number of unique logged-in users." > "$metrics_file"
    echo "# TYPE logged_in_users gauge" >> "$metrics_file"
    echo "logged_in_users $unique_user_count" >> "$metrics_file"

    # Write a separate metric for each logged-in user with a label
    echo "# HELP logged_in_user_details Indicates if a specific user is currently logged in (1 = logged in)." >> "$metrics_file"
    echo "# TYPE logged_in_user_details gauge" >> "$metrics_file"
    for user in $unique_user_names; do
        echo "logged_in_user_details{username=\"$user\"} 1" >> "$metrics_file"
    done
}

# Main loop - runs indefinitely, updating the metrics every 1 second (adjust as needed)
while true; do
    collect_logged_in_users
    sleep 1  # Sleep for 1 second before updating again
done

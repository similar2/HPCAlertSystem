#!/bin/bash
# collect_metrics.sh - Script to collect and write metrics in a loop

# Directory to write metrics
METRICS_DIR="./metrics"

EXPOSE_INTERVAL=1

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

collect_gpu_usage() {
    # Path to the metrics file
    metrics_file="$METRICS_DIR/GPU_usage.prom"
    touch "$metrics_file"

    # Check if nvidia-smi command is available
    if ! command -v nvidia-smi &> /dev/null; then
        echo "nvidia-smi command not found, skipping GPU data collection."
        return
    fi

    # Initialize arrays to store metric samples
    declare -a gpu_utilization_samples
    declare -a gpu_memory_utilization_samples

    # Use nvidia-smi to get utilization metrics
    while IFS=',' read -r index gpu_util mem_util; do
        # Remove leading/trailing whitespace
        index=$(echo "$index" | xargs)
        gpu_util=$(echo "$gpu_util" | xargs)
        mem_util=$(echo "$mem_util" | xargs)

        # Store samples in arrays
        gpu_utilization_samples+=("gpu_utilization_percentage{gpu=\"$index\"} $gpu_util")
        gpu_memory_utilization_samples+=("gpu_memory_utilization_percentage{gpu=\"$index\"} $mem_util")
    done < <(nvidia-smi --query-gpu=index,utilization.gpu,utilization.memory --format=csv,noheader,nounits)

    # Overwrite the file with the new data
    {
        # Output gpu_utilization_percentage metrics
        echo "# HELP gpu_utilization_percentage The current GPU utilization percentage."
        echo "# TYPE gpu_utilization_percentage gauge"
        for sample in "${gpu_utilization_samples[@]}"; do
            echo "$sample"
        done
        echo

        # Output gpu_memory_utilization_percentage metrics
        echo "# HELP gpu_memory_utilization_percentage The current GPU memory utilization percentage."
        echo "# TYPE gpu_memory_utilization_percentage gauge"
        for sample in "${gpu_memory_utilization_samples[@]}"; do
            echo "$sample"
        done
    } > "$metrics_file"
}

# Main loop - runs indefinitely, updating the metrics every 1 second (adjust as needed)
while true; do
    collect_logged_in_users
    collect_gpu_usage
    sleep "$EXPOSE_INTERVAL"  # Sleep for 1 second before updating again
done

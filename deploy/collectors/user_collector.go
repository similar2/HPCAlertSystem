package main

import (
	"fmt"
	"io/ioutil"
	"os"
	"os/exec"
	"strconv"
	"time"
)

func main() {
	// Read environment variables for output path and sleep interval
	outputPath := os.Getenv("OUTPUT_PATH")
	if outputPath == "" {
		outputPath = "/metrics/user_count.prom" // Default path if OUTPUT_PATH is not set
	}

	// Default sleep interval is 1 second, can be overridden by SLEEP_INTERVAL environment variable
	sleepInterval, err := strconv.Atoi(os.Getenv("SLEEP_INTERVAL"))
	if err != nil || sleepInterval <= 0 {
		sleepInterval = 1 // Default to 1 second if not set or invalid
	}

	for {
		// Count logged-in users using `who` command
		cmd := exec.Command("who")
		output, err := cmd.Output()
		if err != nil {
			fmt.Println("Error executing 'who' command:", err)
			return
		}

		// Number of logged-in users (split by newlines)
		userCount := len(output)

		// Prepare metric data in Prometheus format
		metric := fmt.Sprintf(`# HELP logged_in_users Number of logged-in users.
        # TYPE logged_in_users gauge
        logged_in_users %d
`, userCount)

		// Write the metric to the specified file
		err = ioutil.WriteFile(outputPath, []byte(metric), 0644)
		if err != nil {
			fmt.Println("Error writing metric file:", err)
			return
		}

		// Sleep for the specified interval
		time.Sleep(time.Duration(sleepInterval) * time.Second)
	}
}

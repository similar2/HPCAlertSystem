package edu.sustech.hpc.util;

import edu.sustech.hpc.exceptions.PromQLValidationException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class PrometheusUtils {

    private static final String PROMETHEUS_URL = "http://172.18.6.108:9090/api/v1/query";

    public static void validatePromQL(String expr) throws PromQLValidationException {
        String query = "query=" + URLEncoder.encode(expr, StandardCharsets.UTF_8);

        try {
            // Create and configure the connection
            URL urlObj = new URL(PROMETHEUS_URL);
            HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setDoOutput(true);

            // Send the request
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = query.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Read the response
            int responseCode = connection.getResponseCode();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(
                    responseCode == 200 ? connection.getInputStream() : connection.getErrorStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                // Parse and analyze the JSON response
                JSONObject jsonResponse = new JSONObject(response.toString());
                if (!jsonResponse.getString("status").equals("success")) {
                    throw new PromQLValidationException("Invalid PromQL Expression: " + jsonResponse);
                } else if (jsonResponse.getJSONObject("data").getJSONArray("result").isEmpty()) {
                    throw new PromQLValidationException("Not Existing Metric Name or No Data Yet");
                }


            }

        } catch (IOException e) {
            throw new PromQLValidationException("Error during PromQL validation", e);
        }
    }
}

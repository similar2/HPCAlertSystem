package edu.sustech.hpc.model.vo;

import com.fasterxml.jackson.databind.JsonNode;
import edu.sustech.hpc.po.AlertSeverity;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PrometheusAlertInfo {
    String jobName, alertName, instance, state;
    LocalDateTime activeTime;
    LocalDateTime resolvedTime;
    AlertSeverity severity;
    String summary;
    JsonNode other;
}

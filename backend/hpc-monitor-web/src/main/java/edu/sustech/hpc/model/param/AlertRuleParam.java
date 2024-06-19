package edu.sustech.hpc.model.param;

import com.fasterxml.jackson.databind.JsonNode;
import edu.sustech.hpc.po.AlertSeverity;
import edu.sustech.hpc.po.HardwareType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AlertRuleParam {
    HardwareType type;
    String metricName;
    JsonNode filters;
    String timeDuration;
    String expr;
    String alertName;
    String description;
    AlertSeverity severity;
}

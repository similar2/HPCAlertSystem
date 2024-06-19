package edu.sustech.hpc.model.vo;

import edu.sustech.hpc.po.HardwareType;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AlertRuleInfo {
    String alertName, summary, alertRule, severity;
    HardwareType type;
    @Builder.Default
    String timeLength = null;

    public AlertRuleInfo(String alertName, String summary, String alertRule,
                  String severity, HardwareType type) {
        this.alertName = alertName;
        this.summary = summary;
        this.alertRule = alertRule;
        this.severity = severity;
        this.type = type;
    }
}

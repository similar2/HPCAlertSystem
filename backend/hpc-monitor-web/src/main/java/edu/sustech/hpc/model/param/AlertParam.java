package edu.sustech.hpc.model.param;

import edu.sustech.hpc.po.AlertSeverity;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AlertParam {
    private String alertName;
    @NotEmpty
    private String description;
    private String solveMethod;
    private String deviceName;
    private AlertSeverity severity;
}

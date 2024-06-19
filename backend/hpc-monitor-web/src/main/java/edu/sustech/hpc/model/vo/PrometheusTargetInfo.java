package edu.sustech.hpc.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PrometheusTargetInfo {
    String jobName, targetAddr, scrapeInterval,
            scrapeTimeout;

    public String toString() {
        return String.join(" ", this.targetAddr, this.scrapeInterval, this.scrapeTimeout);
    }
}

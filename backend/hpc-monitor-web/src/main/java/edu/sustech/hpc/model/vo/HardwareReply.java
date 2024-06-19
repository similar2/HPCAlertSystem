package edu.sustech.hpc.model.vo;

import edu.sustech.hpc.po.Hardware;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HardwareReply {
    PrometheusTargetInfo prometheusTargetInfo;
    HardwareInfo hardwareInfo;
    ServerInfo serverInfo;
    DeviceInfo deviceInfo;
    ClusterInfo clusterInfo;
}

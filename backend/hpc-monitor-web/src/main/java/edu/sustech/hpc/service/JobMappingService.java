package edu.sustech.hpc.service;

import edu.sustech.hpc.model.vo.HardwareInfo;
import edu.sustech.hpc.model.vo.PrometheusTargetInfo;
import edu.sustech.hpc.po.Hardware;
import edu.sustech.hpc.po.HardwareType;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class JobMappingService {
    @Resource
    private BmcService bmcService;
    @Resource
    private HostService hostService;

    public JobService getJobService(HardwareType hardwareType) {
        return switch (hardwareType) {
            case BMC -> bmcService;
            case HOST -> hostService;
            default -> null;
        };
    }

    public JobService getJobService(String jobName) {
        return switch (jobName) {
            case "ipmi_exporter" -> bmcService;
            case "node_exporter" -> hostService;
            default -> null;
        };
    }

    public HardwareType getHardwareType(String jobName) {
        return getJobService(jobName)._getHardwareType();
    }

    public HardwareInfo getHardwareInfo(PrometheusTargetInfo prometheusTargetInfo) {
        return new HardwareInfo(getHardwarePo(prometheusTargetInfo));
    }

    public Hardware getHardwarePo(PrometheusTargetInfo prometheusTargetInfo) {
        return getJobService(prometheusTargetInfo.getJobName())
                ._getHardwarePo(prometheusTargetInfo);
    }
}

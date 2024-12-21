package edu.sustech.hpc.service;

import edu.sustech.hpc.po.HardwareType;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

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
        };
    }

    public JobService getJobService(String jobName) {
        return switch (jobName) {
            case "ipmi_exporter", "ipmi" -> bmcService;
            case "node_exporter", "node" -> hostService;
            default -> null;
        };
    }

    public HardwareType getHardwareType(String jobName) {
        return getJobService(jobName)._getHardwareType();
    }

}

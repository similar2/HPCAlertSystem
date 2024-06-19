package edu.sustech.hpc.model.vo;

import edu.sustech.hpc.po.Hardware;
import edu.sustech.hpc.po.HardwareType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HardwareInfo {
    private HardwareType type;
    private String ip, name;
    private Integer serverId, hardwareId;

    public HardwareInfo(Hardware hardware) {
        this.type = hardware.getType();
        this.ip = hardware.getIp();
        this.name = hardware.getName();
        this.serverId = hardware.getServerId();
        this.hardwareId = hardware.getId();
    }
}

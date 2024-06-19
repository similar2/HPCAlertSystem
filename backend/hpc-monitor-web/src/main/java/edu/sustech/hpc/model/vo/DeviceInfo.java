package edu.sustech.hpc.model.vo;

import edu.sustech.hpc.po.Device;
import edu.sustech.hpc.po.DeviceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeviceInfo {
    private DeviceType type;
    private String name;
    private Integer id, clusterId;

    public DeviceInfo (Device device) {
        type = device.getType();
        name = device.getName();
        id = device.getId();
        clusterId = device.getClusterId();
    }
}

package edu.sustech.hpc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import edu.sustech.hpc.annotation.PathController;
import edu.sustech.hpc.model.param.DeviceAlertParam;
import edu.sustech.hpc.result.ApiResponse;
import edu.sustech.hpc.service.DeviceAlertService;
import edu.sustech.hpc.service.DeviceService;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@PathController("/api/device-alerts")
public class DeviceAlertController {

    @Resource
    private DeviceAlertService deviceAlertService;

    @Resource
    private DeviceService deviceService;

    /**
     * 获取所有告警信息，并附加额外的设备信息
     */
    @GetMapping
    public ApiResponse<List<DeviceAlertParam>> all() {
        List<DeviceAlertParam> alerts = deviceAlertService.all();
        List<DeviceAlertParam> enrichedAlerts = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();

        for (DeviceAlertParam alert : alerts) {
            DeviceAlertParam alertParam = new DeviceAlertParam();
            alertParam.setId(alert.getId())
                    .setDeviceId(alert.getDeviceId())
                    .setAlertTime(alert.getAlertTime())
                    .setAlertLevel(alert.getAlertLevel())
                    .setDescription(alert.getDescription())
                    .setAlertStatus(alert.getAlertStatus())
                    .setResolveMethod(alert.getResolveMethod())
                    .setResolveTime(alert.getResolveTime())
                    .setResponsiblePerson(alert.getResponsiblePerson());

            // 添加扩展字段（模拟获取设备相关的附加信息）
            ObjectNode other = objectMapper.createObjectNode();
            other.put("deviceName", deviceService.get(alert.getDeviceId()).getName());
            alertParam.setOther(other);

            enrichedAlerts.add(alertParam);
        }
        return ApiResponse.success(enrichedAlerts);
    }

    /**
     * 获取单个告警信息
     */
    @GetMapping("/{id}")
    public ApiResponse<DeviceAlertParam> get(@PathVariable Integer id) {
        DeviceAlertParam alert = deviceAlertService.get(id);
        if (alert == null) {
            return ApiResponse.success(null);
        }

        DeviceAlertParam alertParam = new DeviceAlertParam();
        alertParam.setId(alert.getId())
                .setDeviceId(alert.getDeviceId())
                .setAlertTime(alert.getAlertTime())
                .setAlertLevel(alert.getAlertLevel())
                .setDescription(alert.getDescription())
                .setAlertStatus(alert.getAlertStatus())
                .setResolveMethod(alert.getResolveMethod())
                .setResolveTime(alert.getResolveTime());

        return ApiResponse.success(alertParam);
    }

    /**
     * 新增告警
     */
    @PostMapping
    public ApiResponse<DeviceAlertParam> add(@Validated @RequestBody DeviceAlertParam alertParam) {
        DeviceAlertParam alert = deviceAlertService.add(alertParam);
        return ApiResponse.success(alert);
    }

    /**
     * 修改告警信息
     */
    @PatchMapping("/{id}")
    public ApiResponse<DeviceAlertParam> modify(
            @PathVariable Integer id,
            @RequestBody DeviceAlertParam alertParam) {
        DeviceAlertParam existingAlert = deviceAlertService.get(id);

        if (alertParam.getAlertTime() != null) {
            existingAlert.setAlertTime(alertParam.getAlertTime());
        }
        if (alertParam.getAlertLevel() != null) {
            existingAlert.setAlertLevel(alertParam.getAlertLevel());
        }
        if (alertParam.getDescription() != null) {
            existingAlert.setDescription(alertParam.getDescription());
        }
        if (alertParam.getAlertStatus() != null) {
            existingAlert.setAlertStatus(alertParam.getAlertStatus());
        }
        if (alertParam.getResolveMethod() != null) {
            existingAlert.setResolveMethod(alertParam.getResolveMethod());
        }
        if (alertParam.getResolveTime() != null) {
            existingAlert.setResolveTime(alertParam.getResolveTime());
        }
        if (alertParam.getResponsiblePerson() != null) {
            existingAlert.setResponsiblePerson(alertParam.getResponsiblePerson());
        }

        DeviceAlertParam updatedAlert = deviceAlertService.modify(id, existingAlert);
        return ApiResponse.success(updatedAlert);
    }

    /**
     * 根据设备ID获取告警信息
     */
    @GetMapping("/device/{deviceId}")
    public ApiResponse<List<DeviceAlertParam>> getAlertsByDeviceId(@PathVariable Integer deviceId) {
        List<DeviceAlertParam> alerts = deviceAlertService.getByDeviceId(deviceId);

        if (alerts == null || alerts.isEmpty()) {
            return ApiResponse.success(null);
        }

        return ApiResponse.success(alerts);
    }


    /**
     * 删除告警
     */
    @DeleteMapping("/{id}")
    public ApiResponse delete(@PathVariable Integer id) {
        deviceAlertService.delete(id);
        return ApiResponse.success();
    }
}

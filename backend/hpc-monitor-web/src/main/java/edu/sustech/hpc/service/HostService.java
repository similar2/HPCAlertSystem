package edu.sustech.hpc.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import edu.sustech.hpc.model.vo.*;
import edu.sustech.hpc.po.Alert;
import edu.sustech.hpc.po.AlertType;
import edu.sustech.hpc.po.Hardware;
import edu.sustech.hpc.po.HardwareType;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class HostService extends JobService {
    String configFile;

    HostService() throws IOException {
        configFile = "/deploy/prometheus/prometheus.yml";
        jobName = "node_exporter";
        hardwareType = HardwareType.HOST;

    }

    @Override
    public HardwareType _getHardwareType() {
        return HardwareType.HOST;
    }

    @Override
    public JsonNode getActivePrometheusAlertInfoOther(JSONObject alertInfo) {
        return null;
    }

    @Override
    public void checkAddActiveAlert(PrometheusAlertInfo prometheusAlertInfo) {
        String generated_alertName = prometheusAlertInfo.getAlertName();
        if (prometheusAlertInfo.getOther().has("name"))
            generated_alertName += "_" + prometheusAlertInfo.getOther().get("name").asText();
        HardwareInfo hardwareInfo = new HardwareInfo(_getHardwarePo(prometheusAlertInfo.getInstance()));
        ServerInfo serverInfo = databaseService.getServerInfoFromHardwareInfo(hardwareInfo);
        DeviceInfo deviceInfo = databaseService.getDeviceInfoFromServerInfo(serverInfo);
        Alert alert = alertService.getActiveAlert(generated_alertName, deviceInfo.getName());
        if (alert == null) {
            alertService.addPrometheusAlert(
                    Alert.builder().alertName(generated_alertName)
                            .createTime(prometheusAlertInfo.getActiveTime())
                            .description(prometheusAlertInfo.getSummary())
                            .severity(prometheusAlertInfo.getSeverity())
                            .deviceName(deviceInfo.getName())
                            .type(AlertType.PROMETHEUS)
                            .build()
                    , prometheusAlertInfo);
        } else {
            alertService.updatePrometheusAlert(alert, prometheusAlertInfo);
        }
    }

    public Hardware _getHardwarePo(String ip) {
        Hardware hardware = hardwareDao.selectOne(new LambdaQueryWrapper<Hardware>()
                .eq(Hardware::getIp, ip));
        return hardware;
    }

    @Override
    public Hardware _getHardwarePo(PrometheusTargetInfo prometheusTargetInfo) {
        return null;
    }
}

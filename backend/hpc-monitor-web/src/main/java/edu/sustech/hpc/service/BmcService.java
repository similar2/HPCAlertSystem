package edu.sustech.hpc.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import edu.sustech.hpc.model.vo.*;
import edu.sustech.hpc.po.*;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
public class BmcService extends JobService {
    String targetConfigFile;

    BmcService() throws IOException {
        targetConfigFile = "/prometheus/targets.yml";
        jobName = "ipmi_exporter";
        hardwareType = HardwareType.BMC;
    }

    public HardwareType _getHardwareType() {
        return HardwareType.BMC;
    }

    public Hardware _getHardwarePo(String ip) {
        Hardware hardware = hardwareDao.selectOne(new LambdaQueryWrapper<Hardware>()
                .eq(Hardware::getIp, ip));
        return hardware;
    }

    public JsonNode getActivePrometheusAlertInfoOther(JSONObject alert) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("name", alert.getJSONObject("labels").getString("name"));
        return objectNode;
    }

    @Override
    public void checkAddActiveAlert(PrometheusAlertInfo prometheusAlertInfo) {
        String generated_alertName = prometheusAlertInfo.getAlertName();
        if (prometheusAlertInfo.getOther().has("name"))
            generated_alertName += "_" + prometheusAlertInfo.getOther().get("name").asText();
        Hardware hardware = _getHardwarePo(prometheusAlertInfo.getInstance());
        Alert alert = alertService.getActiveAlert(generated_alertName, hardware.getDeviceName());
        if (alert == null) {
            alertService.addPrometheusAlert(
                    Alert.builder().alertName(generated_alertName)
                            .createTime(prometheusAlertInfo.getActiveTime())
                            .description(prometheusAlertInfo.getSummary())
                            .severity(prometheusAlertInfo.getSeverity())
                            .deviceName(hardware.getDeviceName())
                            .type(AlertType.PROMETHEUS)
                            .build()
                    , prometheusAlertInfo);
        } else {
            alertService.updatePrometheusAlert(alert, prometheusAlertInfo);
        }
    }
}

package edu.sustech.hpc.service;

import cn.hutool.core.io.resource.ClassPathResource;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import edu.sustech.hpc.model.vo.*;
import edu.sustech.hpc.po.*;
import edu.sustech.hpc.util.Utils;
import edu.sustech.hpc.util.YamlObj;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class BmcService extends JobService {
    String targetConfigFile;

    BmcService() throws IOException {
        targetConfigFile = "/prometheus/targets.yml";
        jobName = "ipmi_exporter";
        hardwareType = HardwareType.BMC;
        readMetricTemplate();
    }

    void readMetricTemplate() throws IOException {
        ClassPathResource res = new ClassPathResource("metric_templates/BMC");
        InputStream inputStream = res.getStream();
        InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(streamReader);
        JSONArray jsonArray = new JSONArray(reader.readLine());
        for (Object json : jsonArray) {
            JSONObject metricInfo = (JSONObject) json;
            addMetric(metricInfo);
        }
    }

    public void addMetric(JSONObject jsonObject) {
        String metricName = jsonObject.getString("name");
        metricNames.add(metricName);
        if (!jsonObject.has("metrics"))
            return;
        JSONArray metrics = jsonObject.getJSONArray("metrics");
        for (Object metric : metrics) {
            JSONObject labels = ((JSONObject) metric).has("labels") ?
                    ((JSONObject) metric).getJSONObject("labels") : null;
            if (labels == null)
                continue;
            addFilterObject(metricName, labels, "name");
            addFilterObject(metricName, labels, "collect");
            addFilterObject(metricName, labels, "type");
        }
    }

    public void addFilterObject(String metricName, JSONObject labels, String key) {
        if (!labels.has(key))
            return;
        String option = labels.getString(key)
                .replaceAll("[0-9]+", "[0-9]*");
        String simplifiedOption = labels.getString(key)
                .replaceAll("[0-9]*", "");
        if (simplifiedOption.endsWith("_"))
            simplifiedOption = simplifiedOption.substring(0, simplifiedOption.length() - 1);
        if (!filterObjects.containsKey(metricName))
            filterObjects.put(metricName, new HashMap<>());
        if (!filterObjects.get(metricName).containsKey(key))
            filterObjects.get(metricName).put(key, new HashMap<>());
        filterObjects.get(metricName).get(key).put(simplifiedOption, option);
    }

    // Return 0 if successful, 1 if failed
    public int addHardware(HardwareInfo hardwareInfo, BmcInfo bmcInfo) {
        YamlObj yamlObj;
        try {
            yamlObj = Utils.getYaml(targetConfigFile);
        } catch (IOException e) {
            return 1;
        }
        ArrayList<Map<String, Object>> obj = (ArrayList<Map<String, Object>>) yamlObj.object;
        List<String> targetList = (List<String>) obj.get(0).get("targets");
        if (targetList.contains(hardwareInfo.getIp())) {
            return 1;
        }
        targetList.add(hardwareInfo.getIp());
        try {
            yamlObj.writeYaml();
        } catch (FileNotFoundException e) {
            return 1;
        }
        return 0;
    }

    public int removeTarget(HardwareInfo hardwareInfo, BmcInfo bmcInfo) {
        YamlObj yamlObj;
        try {
            yamlObj = Utils.getYaml(targetConfigFile);
        } catch (IOException e) {
            return 1;
        }
        ArrayList<Map<String, Object>> obj = (ArrayList<Map<String, Object>>) yamlObj.object;
        List<String> targetList = (List<String>) obj.get(0).get("targets");
        if (!targetList.contains(hardwareInfo.getIp())) {
            return 1;
        }
        targetList.remove(hardwareInfo.getIp());
        try {
            yamlObj.writeYaml();
        } catch (FileNotFoundException e) {
            return 1;
        }
        return 0;
    }

    public HardwareType _getHardwareType() {
        return HardwareType.BMC;
    }

    @Override
    public Hardware _getHardwarePo(PrometheusTargetInfo prometheusTargetInfo) {
        Hardware hardware = hardwareDao.selectOne(new LambdaQueryWrapper<Hardware>()
                .eq(Hardware::getIp, prometheusTargetInfo.getTargetAddr()));
        return hardware;
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
}

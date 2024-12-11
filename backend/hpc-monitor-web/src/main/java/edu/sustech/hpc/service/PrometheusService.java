package edu.sustech.hpc.service;

import com.google.common.io.CharStreams;
import edu.sustech.hpc.handler.ApiException;
import edu.sustech.hpc.model.param.AlertRuleParam;
import edu.sustech.hpc.model.vo.*;
import edu.sustech.hpc.po.AlertSeverity;
import edu.sustech.hpc.po.HardwareType;
import edu.sustech.hpc.util.PrometheusUtils;
import edu.sustech.hpc.util.Utils;
import edu.sustech.hpc.util.YamlObj;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class PrometheusService {

    @Resource
    private JobMappingService jobMappingService;

//    @Scheduled(fixedRate = 60000)
    public void getAlertFromPrometheus() throws IOException {
        log.info("Checking active alerts from Prometheus");
//        List<PrometheusAlertInfo> prometheusAlertInfoList = getActiveAlerts();
//        for (PrometheusAlertInfo prometheusAlertInfo : prometheusAlertInfoList) {
//            jobMappingService.getJobService(prometheusAlertInfo.getJobName()).checkAddActiveAlert(prometheusAlertInfo);
//        }
    }

    public Map<String, List<String>> getFilterOptionsByMetricName(HardwareType hardwareType, String metricName) {
        JobService jobService = jobMappingService.getJobService(hardwareType);
        Map<String, List<String>> map = new HashMap<>();
        if (jobService.filterObjects.get(metricName) == null) {
            return Map.of();
        }
        for (String key : jobService.filterObjects.get(metricName).keySet()) {
            map.put(key, new ArrayList<>(jobService.filterObjects.get(metricName).get(key).keySet()));
        }
        return map;
    }

    public void reloadPrometheus() throws IOException, InterruptedException {
        String command = "curl -XPOST http://172.18.6.108:9090/-/reload";
        Process proc = Runtime.getRuntime().exec(command);
        proc.waitFor();
    }

    public List<String> getMetricNames(HardwareType hardwareType) {
        return jobMappingService.getJobService(hardwareType).metricNames;
    }

    public int deleteAlertRule(HardwareType hardwareType, String alertName) {
        JobService jobService = jobMappingService.getJobService(hardwareType);
        String jobName = jobService.jobName;
        YamlObj yamlObj;
        try {
            yamlObj = Utils.getYaml(jobService.ruleConfigFile);
        } catch (IOException e) {
            return 1;
        }
        ArrayList<Map<String, Object>> groupList =
                (ArrayList<Map<String, Object>>) (((Map<String, Object>) yamlObj.object).get("groups"));
        Map<String, Object> targetGroup = null;
        for (Map<String, Object> group : groupList) {
            if (group.get("name").equals(jobName)) {
                targetGroup = group;
                break;
            }
        }
        if (targetGroup == null) {
            return 1;
        }
        List<Map<String, Object>> rulesList =
                (List<Map<String, Object>>) targetGroup.get("rules");
        for (Map<String, Object> rule : rulesList) {
            if (rule.get("alert").equals(alertName)) {
                rulesList.remove(rule);
                break;
            }
        }
        try {
            yamlObj.writeYaml();
        } catch (FileNotFoundException e) {
            return 1;
        }
        try {
            reloadPrometheus();
        } catch (IOException | InterruptedException e) {
            return 1;
        }
        return 0;
    }

    public int addAlertRule(AlertRuleParam alertRuleParam) {
        JobService jobService = jobMappingService.getJobService(alertRuleParam.getType());
        String jobName = jobService.jobName;
        YamlObj yamlObj;
        try {
            yamlObj = Utils.getYaml(jobService.ruleConfigFile);
        } catch (IOException e) {
            return 1;
        }
        ArrayList<Map<String, Object>> groupList =
                (ArrayList<Map<String, Object>>) (((Map<String, Object>) yamlObj.object).get("groups"));
        Map<String, Object> targetGroup = null;
        for (Map<String, Object> group : groupList) {
            if (group.get("name").equals(jobName)) {
                targetGroup = group;
                break;
            }
        }
        if (targetGroup == null) {
            return 1;
        }
        List<Map<String, Object>> rulesList =
                (List<Map<String, Object>>) targetGroup.get("rules");
        if (rulesList == null) {
            rulesList = new ArrayList<>();
            targetGroup.put("rules", rulesList);  // Ensure the new list is linked to targetGroup
        }

        Map<String, Object> rule = new HashMap<>();
        rule.put("alert", alertRuleParam.getAlertName());
//        String expr =
//                alertRuleParam.getMetricName() + "{job=\"" +
//                        jobName + "\"" + jobService.getAlertRuleFilteredExpr(alertRuleParam)
//                        + "} " + alertRuleParam.getExpr();
        String expr = alertRuleParam.getExpr();
        // ** Validate the PromQL expression before proceeding **

        PrometheusUtils.validatePromQL(expr);  // Using the utility method

        rule.put("expr", expr);
        if (alertRuleParam.getTimeDuration() != null)
            rule.put("for", alertRuleParam.getTimeDuration());
        rule.put("labels", Map.of("severity", alertRuleParam.getSeverity().name()));
        rule.put("annotations", Map.of("summary", alertRuleParam.getDescription()));
        rulesList.add(rule);
        try {
            yamlObj.writeYaml();
        } catch (FileNotFoundException e) {
            return 1;
        }
        try {
            reloadPrometheus();
        } catch (IOException | InterruptedException e) {
            return 1;
        }
        return 0;
    }

    public AlertRuleInfo getAlertRuleInfo(String groupName, JSONObject rule) {
        HardwareType type = jobMappingService.getHardwareType(groupName);
        String query = rule.getString("query");
        query = query.replace("~", "");
//        query = query.replace("[0-9]*", "");
        if (query.endsWith("_"))
            query = query.substring(0, query.length() - 1);
        AlertRuleInfo alertRuleInfo = new AlertRuleInfo(
                rule.getString("name"),
                rule.getJSONObject("annotations").getString("summary"),
                query,
                rule.getJSONObject("labels").getString("severity"),
                type
        );
        if (rule.has("duration")) {
            alertRuleInfo.setTimeLength(String.valueOf(rule.getInt("duration")));
        }
        return alertRuleInfo;
    }

    public List<AlertRuleInfo> getAllAlertRules() throws IOException {
        JSONObject response = new JSONObject(CharStreams.toString(new InputStreamReader(
                new URL("http://172.18.6.108:9090/api/v1/rules").openStream(), StandardCharsets.UTF_8)));
        JSONArray ruleGroups = response.getJSONObject("data")
                .getJSONArray("groups");
        List<AlertRuleInfo> alertRuleInfoList = new ArrayList<>();
        for (int i = 0; i < ruleGroups.length(); i++) {
            JSONArray rules = ruleGroups.getJSONObject(i).getJSONArray("rules");
            String groupName = ruleGroups.getJSONObject(i).getString("name");
            for (int j = 0; j < rules.length(); j++) {
                JSONObject rule = rules.getJSONObject(j);
                if (!rule.getString("type").equals("alerting"))
                    continue;
                AlertRuleInfo alertRuleInfo =
                        getAlertRuleInfo(groupName, rule);
                alertRuleInfoList.add(alertRuleInfo);
            }
        }
        return alertRuleInfoList;
    }

//    public List<PrometheusTargetInfo> _getAllTargets() throws IOException {
//        JSONObject response = new JSONObject(CharStreams.toString(new InputStreamReader(
//                new URL("http://172.18.6.108:9090/api/v1/targets").openStream(), "UTF-8")));
//        JSONArray targets = response.getJSONObject("data").getJSONArray("activeTargets");
//        List<PrometheusTargetInfo> prometheusTargetInfoList = new ArrayList<>();
//        for (int i = 0; i < targets.length(); i++) {
//            JSONObject target = targets.getJSONObject(i).getJSONObject("discoveredLabels");
//            PrometheusTargetInfo prometheusTargetInfo = new PrometheusTargetInfo(
//                    target.getString("job"),
//                    target.getString("__address__"),
//                    target.getString("__scrape_interval__"),
//                    target.getString("__scrape_timeout__")
//            );
//            prometheusTargetInfoList.add(prometheusTargetInfo);
//        }
//        return prometheusTargetInfoList;
//    }

//    public List<HardwareReply> getAllHardwareReply(String jobName) throws IOException {
//        List<PrometheusTargetInfo> prometheusTargetInfoList = _getAllTargets();
//        List<HardwareReply> hardwareReplyList = new ArrayList<>();
//        for (PrometheusTargetInfo prometheusTargetInfo : prometheusTargetInfoList) {
//            if (jobName != null && !prometheusTargetInfo.getJobName().equals(jobName))
//                continue;
//            HardwareInfo hardwareInfo = jobMappingService.getHardwareInfo(prometheusTargetInfo);
//            ServerInfo serverInfo = databaseService.getServerInfoFromHardwareInfo(hardwareInfo);
//            DeviceInfo deviceInfo = databaseService.getDeviceInfoFromServerInfo(serverInfo);
//            ClusterInfo clusterInfo = databaseService.getClusterInfoFromDeviceInfo(deviceInfo);
//            hardwareReplyList.add(new HardwareReply(
//                    prometheusTargetInfo, hardwareInfo, serverInfo,
//                    deviceInfo, clusterInfo));
//        }
//        return hardwareReplyList;
//    }

//    public List<HardwareReply> getAllHardwareReply() throws IOException {
//        return getAllHardwareReply(null);
//    }
//
//    public List<PrometheusTargetInfo> getTargets(String jobName) throws IOException {
//        List<PrometheusTargetInfo> prometheusTargetInfoList = _getAllTargets();
//        if (jobName == null)
//            return prometheusTargetInfoList;
//        List<PrometheusTargetInfo> filteredPrometheusTargetInfoList = new ArrayList<>();
//        for (PrometheusTargetInfo prometheusTargetInfo : prometheusTargetInfoList)
//            if (prometheusTargetInfo.getJobName().equals(jobName))
//                filteredPrometheusTargetInfoList.add(prometheusTargetInfo);
//        return filteredPrometheusTargetInfoList;
//    }

//    public List<PrometheusAlertInfo> getActiveAlerts() throws IOException {
//        JSONObject response = new JSONObject(CharStreams.toString(new InputStreamReader(
//                new URL("http://172.18.6.108:9090/api/v1/alerts").openStream(), StandardCharsets.UTF_8)));
//        JSONArray alerts = response.getJSONObject("data").getJSONArray("alerts");
//        List<PrometheusAlertInfo> prometheusAlertInfoList = new ArrayList<>();
//        for (int i = 0; i < alerts.length(); i++) {
//            JSONObject alert = alerts.getJSONObject(i);
//            JSONObject labels = alert.getJSONObject("labels");
//            if (!alert.getString("state").equals("firing"))
//                continue;
//
//            String jobName = labels.getString("job");
//            PrometheusAlertInfo prometheusAlertInfo = PrometheusAlertInfo.builder()
//                    .instance(labels.getString("instance"))
//                    .jobName(jobName)
//                    .alertName(labels.getString("alertname"))
//                    .activeTime(ZonedDateTime.parse(alert.getString("activeAt")).toLocalDateTime())
//                    .severity(AlertSeverity.valueOf(labels.getString("severity")))
//                    .summary(alert.getJSONObject("annotations").getString("summary"))
//                    .other(jobMappingService.getJobService(jobName).getActivePrometheusAlertInfoOther(alert)).build();
//            prometheusAlertInfoList.add(prometheusAlertInfo);
//        }
//        return prometheusAlertInfoList;
//    }

    public static void main(String[] argv) {
        PrometheusService prometheusService = new PrometheusService();
        System.out.println(prometheusService.jobMappingService.getJobService("ipmi_exporter"));
    }

    public void parseAlerts(String alertPayload) {
        try {
            // 解析Json串
            List<PrometheusAlertInfo> prometheusAlertInfoList = parseAlertManagerPayload(alertPayload);

            for (PrometheusAlertInfo prometheusAlertInfo : prometheusAlertInfoList) {
                jobMappingService.getJobService(prometheusAlertInfo.getJobName()).checkAddActiveAlert(prometheusAlertInfo);
            }
        } catch (Exception e) {
            log.error("Invalid alert payload", e);
            throw new ApiException(201,"Invalid alert payload");
        }
    }

    public List<PrometheusAlertInfo> parseAlertManagerPayload(String alertPayload) {
        List<PrometheusAlertInfo> prometheusAlertInfoList = new ArrayList<>();

        JSONObject alertJson = new JSONObject(alertPayload);
        JSONArray alerts = alertJson.getJSONArray("alerts");

        for (int i = 0; i < alerts.length(); i++) {
            JSONObject alert = alerts.getJSONObject(i);
            JSONObject labels = alert.getJSONObject("labels");
            JSONObject annotations = alert.getJSONObject("annotations");

            String jobName = labels.getString("job");

            PrometheusAlertInfo prometheusAlertInfo = PrometheusAlertInfo.builder()
                    .instance(labels.getString("instance"))
                    .jobName(labels.getString("job"))
                    .alertName(labels.getString("alertname"))
                    .activeTime(parseTime(alert.getString("startsAt")))
                    .resolvedTime(parseTime(alert.getString("endsAt")))
                    .severity(AlertSeverity.valueOf(labels.getString("severity").toUpperCase()))
                    .summary(annotations.getString("summary"))
                    .state(alert.getString("status"))
                    .other(jobMappingService.getJobService(jobName).getActivePrometheusAlertInfoOther(alert))
                    .build();

            prometheusAlertInfoList.add(prometheusAlertInfo);
        }

        return prometheusAlertInfoList;
    }

    private LocalDateTime parseTime(String time) {
        return ZonedDateTime.parse(time).toLocalDateTime();
    }
}

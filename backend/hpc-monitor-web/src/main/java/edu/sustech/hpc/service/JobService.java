package edu.sustech.hpc.service;

import com.fasterxml.jackson.databind.JsonNode;
import edu.sustech.hpc.dao.AlertDao;
import edu.sustech.hpc.model.param.AlertRuleParam;
import edu.sustech.hpc.model.vo.AlertRuleInfo;
import edu.sustech.hpc.model.vo.HardwareInfo;
import edu.sustech.hpc.model.vo.PrometheusAlertInfo;
import edu.sustech.hpc.model.vo.PrometheusTargetInfo;
import edu.sustech.hpc.po.Hardware;
import edu.sustech.hpc.po.HardwareType;
import edu.sustech.hpc.util.Utils;
import edu.sustech.hpc.util.YamlObj;
import jakarta.annotation.Resource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import edu.sustech.hpc.dao.HardwareDao;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public abstract class JobService {
    String jobName;
    HardwareType hardwareType;
    String ruleConfigFile = "/prometheus/rule.yml";
    Map<String, Map<String, Map<String, String>>> filterObjects = new HashMap<>();
    List<String> metricNames = new ArrayList<>();
    @Resource
    HardwareDao hardwareDao;

    @Resource
    AlertDao alertDao;

    @Resource
    AlertService alertService;

    @Resource
    protected DatabaseService databaseService;

    public abstract HardwareType _getHardwareType();

    public abstract JsonNode getActivePrometheusAlertInfoOther(JSONObject alertInfo);

    public abstract void checkAddActiveAlert(PrometheusAlertInfo prometheusAlertInfo);

    public int addAlert(AlertRuleInfo alertRuleInfo) {
        YamlObj yamlObj;
        try {
            yamlObj = Utils.getYaml(ruleConfigFile);
        } catch (IOException e) {
            return 1;
        }
        ArrayList<Map<String, Object>> rules = (ArrayList<Map<String, Object>>)
                ((ArrayList<Map<String, Object>>)
                        ((Map<String, Object>) yamlObj.object).get("groups")).get(0).get("rules");
        Map<String, Object> ruleMap = Map.of(
                "expr", alertRuleInfo.getAlertRule(),
                "alert", alertRuleInfo.getAlertName(),
                "annotations", Map.of("summary", alertRuleInfo.getSummary()),
                "labels", Map.of("severity", alertRuleInfo.getSeverity())
        );
        if(alertRuleInfo.getTimeLength() != null)
            ruleMap.put("for", alertRuleInfo.getTimeLength());
        rules.add(ruleMap);
        try {
            yamlObj.writeYaml();
        } catch (FileNotFoundException e) {
            return 1;
        }
        return 0;
    }

    public abstract String getAlertRuleFilteredExpr(AlertRuleParam alertRuleParam);

    public abstract Hardware _getHardwarePo(PrometheusTargetInfo prometheusTargetInfo);
}

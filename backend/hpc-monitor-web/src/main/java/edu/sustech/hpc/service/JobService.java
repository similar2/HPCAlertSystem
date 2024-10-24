package edu.sustech.hpc.service;

import com.fasterxml.jackson.databind.JsonNode;
import edu.sustech.hpc.dao.AlertDao;
import edu.sustech.hpc.model.param.AlertRuleParam;
import edu.sustech.hpc.model.vo.PrometheusAlertInfo;
import edu.sustech.hpc.model.vo.PrometheusTargetInfo;
import edu.sustech.hpc.po.Hardware;
import edu.sustech.hpc.po.HardwareType;
import jakarta.annotation.Resource;

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
    //String ruleConfigFile = "/prometheus/rule.yml";
    String ruleConfigFile = "E:\\project\\HPCAlertSystem\\deploy\\prometheus\\rule.yml";


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
    public abstract Hardware _getHardwarePo(PrometheusTargetInfo prometheusTargetInfo);
}

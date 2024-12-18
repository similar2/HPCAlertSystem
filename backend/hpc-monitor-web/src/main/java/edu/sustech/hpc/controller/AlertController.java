package edu.sustech.hpc.controller;

import edu.sustech.hpc.annotation.PathController;
import edu.sustech.hpc.model.dto.AlertPageQueryDTO;
import edu.sustech.hpc.model.param.AlertParam;
import edu.sustech.hpc.model.param.AlertRuleParam;
import edu.sustech.hpc.model.vo.AlertRuleInfo;
import edu.sustech.hpc.result.ApiResponse;
import edu.sustech.hpc.po.Alert;
import edu.sustech.hpc.po.HardwareType;
import edu.sustech.hpc.result.PageResult;
import edu.sustech.hpc.service.AlertService;
import edu.sustech.hpc.service.PrometheusService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@PathController("/alerts")
@CrossOrigin
@Slf4j
public class AlertController {
    @PostMapping
    public ApiResponse receiveAlerts(@RequestBody String alertPayload) {
        log.info("Receive alert: {}", alertPayload);
        prometheusService.parseAlerts(alertPayload);
        return ApiResponse.success();
    }

    @Resource
    private AlertService alertService;
    @Resource
    private PrometheusService prometheusService;

    @GetMapping("/rules")
    public ApiResponse getAlertRules() {
        List<AlertRuleInfo> alertRuleInfoList;
        try {
            alertRuleInfoList = prometheusService.getAllAlertRules();
        } catch (IOException e) {
            return ApiResponse.internalServerError();
        }
        JSONArray jsonArray = new JSONArray(alertRuleInfoList);
        String message = jsonArray.toString();
        return ApiResponse.success(message);
    }

    @PostMapping("/rules/add")
    public ApiResponse addAlertRule(@Validated @RequestBody AlertRuleParam alertRuleParam) {
        return ApiResponse.success(prometheusService.addAlertRule(alertRuleParam));
    }

    @PostMapping("/rules/delete")
    public ApiResponse deleteAlertRule(@RequestParam(name = "type") String type,
                                       @RequestParam(name = "alert_name") String alertName) {
        return ApiResponse.success(prometheusService.deleteAlertRule(
                HardwareType.valueOf(type), alertName));
    }

    @GetMapping("/rules/metric_names")
    public ApiResponse getAlertRules(
            @RequestParam(name = "type") String type) {
        List<String> metricNames = prometheusService.getMetricNames(HardwareType.valueOf(type));
        JSONArray json = new JSONArray(metricNames);
        String message = json.toString();
        return ApiResponse.success(message);
    }

    @GetMapping("/rules/metric_filters")
    public ApiResponse getAlertRules(
            @RequestParam(name = "type") String type,
            @RequestParam(name = "metric") String metricName) {
        Map<String, List<String>> filterOptionList;
        filterOptionList = prometheusService.getFilterOptionsByMetricName(
                HardwareType.valueOf(type), metricName
        );
        JSONObject json = new JSONObject(filterOptionList);
        String message = json.toString();
        return ApiResponse.success(message);
    }

    @GetMapping("/all")
    public ApiResponse getActiveAlerts(
            @RequestParam(name = "device_name", required = false) String deviceName,
            @RequestParam(name = "start_time", required = false) LocalDateTime startTime,
            @RequestParam(name = "end_time", required = false) LocalDateTime endTime,
            @RequestParam(name = "solved", required = false) Boolean solved
    ) {
        List<Alert> alerts = alertService.getAll(deviceName, startTime, endTime, solved);
        JSONArray jsonArray = new JSONArray(alerts);
        String message = jsonArray.toString();
        return ApiResponse.success(message);
    }

    @GetMapping("/page")
    public ApiResponse<PageResult> page(AlertPageQueryDTO alertPageQueryDTO) {
        return ApiResponse.success(alertService.pageQuery(alertPageQueryDTO));
    }

    @PostMapping("/add")
    public ApiResponse<Alert> add(@Validated @RequestBody AlertParam alertParam) {
        Alert alert = alertService.add(alertParam);
        return ApiResponse.success(alert);
    }

    @PostMapping("/solve/{id}")
    public ApiResponse<Alert> solve(@PathVariable Integer id, @RequestParam String solveMethod) {
        Alert alert = alertService.solve(id, solveMethod);
        return ApiResponse.success(alert);
    }

    @GetMapping("/get/{id}")
    public ApiResponse<Alert> get(@PathVariable Integer id) {
        Alert alert = alertService.get(id);
        return ApiResponse.success(alert);
    }

//    @PostMapping("/modify/{id}")
//    public ApiResponse<Alert> modify(@PathVariable Integer id,
//                                     @RequestBody @Validated AlertParam alertParam){
//        Alert alert = alertService.modify(id,
//                                        alertParam.getDescription(),
//                                        alertParam.getDeviceName(),
//                                        alertParam.getSolveMethod());
//        return ApiResponse.success(alert);
//    }
}

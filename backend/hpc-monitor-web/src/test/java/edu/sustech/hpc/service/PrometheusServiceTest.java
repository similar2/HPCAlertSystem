package edu.sustech.hpc.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import edu.sustech.hpc.model.param.AlertRuleParam;
import edu.sustech.hpc.po.AlertSeverity;
import edu.sustech.hpc.po.HardwareType;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
public class PrometheusServiceTest {
    @Resource
    private PrometheusService prometheusService;

//    @Test
//    public void getAllHardwareReplyTest() throws IOException {
//        System.out.println(prometheusService.getAllHardwareReply());
//    }

    @Test
    public void getAllAlertRulesTest() throws IOException {
        System.out.println(prometheusService.getAllAlertRules());
    }

    @Test
    public void getAllActiveAlertsTest() throws IOException {
        System.out.println(prometheusService.getActiveAlerts());
    }

    @Test
    public void reloadPrometheusTest() throws IOException, InterruptedException {
        prometheusService.reloadPrometheus();
    }

    @Test
    public void getFilterOptionsByMetricName() {
        HardwareType bmc = HardwareType.BMC;
        prometheusService.getFilterOptionsByMetricName(bmc, "ipmi_fan_speed");
    }

    @Test
    public void addAlertRuleTest() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("name", "CPU_Temp");

        AlertRuleParam alertRuleParam =
                AlertRuleParam.builder().alertName("HighCPUTemp")
                        .expr("up")
                        .description("CPU temperatue too high")
                        .type(HardwareType.BMC)
                        .severity(AlertSeverity.CRITICAL)
                        .timeDuration("5m")
                        .build();
        prometheusService.addAlertRule(alertRuleParam);
    }

    @Test
    public void addPrometheusAlertTest() throws IOException {
        prometheusService.getAlertFromPrometheus();
    }
}

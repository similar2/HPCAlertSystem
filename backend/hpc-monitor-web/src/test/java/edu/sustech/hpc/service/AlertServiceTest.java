package edu.sustech.hpc.service;

import edu.sustech.hpc.model.param.AlertParam;
import edu.sustech.hpc.po.AlertSeverity;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
public class AlertServiceTest {
    @Resource
    private AlertService alertService;

    @Test
    public void addAlertTest() {
        alertService.add(
          AlertParam.builder().alertName("空调故障")
                  .description("空调无法制冷")
                  .deviceName("空调03号")
                  .severity(AlertSeverity.CRITICAL)
                  .build()
        );
    }
}

package edu.sustech.hpc.service;

import cn.hutool.core.io.resource.ClassPathResource;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.nio.charset.StandardCharsets;

@SpringBootTest
public class BmcServiceTest {
    @Resource
    private BmcService bmcService;

    @Test
    public void testMetricRead() throws IOException {
//        bmcService.readMetricTemplate();
        System.out.println(bmcService.filterObjects.get("ipmi_fan_speed_rpm"));
    }
}

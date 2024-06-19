package edu.sustech.hpc.service;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class JobMappingServiceTest {
    @Resource
    private JobMappingService jobMappingService;

    @Test
    public void getHardwareTypeTest() {
        System.out.println(jobMappingService.getJobService("ipmi_exporter"));
    }
}

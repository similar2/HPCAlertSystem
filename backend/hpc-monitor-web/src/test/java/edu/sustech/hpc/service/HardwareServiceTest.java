package edu.sustech.hpc.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import edu.sustech.hpc.model.param.HardwareParam;
import edu.sustech.hpc.model.vo.HardwareInfo;
import edu.sustech.hpc.po.HardwareType;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class HardwareServiceTest {
    @Resource
    private HardwareService hardwareService;

    @Test
    public void addTest() {
        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode objectNode = objectMapper.createObjectNode();

        objectNode.put("username", "ADMIN");
        objectNode.put("password", "ADMIN");
        objectNode.put("port", 623);
        hardwareService.add(
                new HardwareParam(null,
                        "bmc4", "11.11.3.104", HardwareType.BMC,
                        5, objectNode)
        );
    }

    @Test
    public void deleteTest() {
        hardwareService.delete(9);
    }
}

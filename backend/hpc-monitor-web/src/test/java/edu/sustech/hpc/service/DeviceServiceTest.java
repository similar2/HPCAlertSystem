package edu.sustech.hpc.service;

import edu.sustech.hpc.model.param.DeviceParam;
import edu.sustech.hpc.po.Assignee;
import edu.sustech.hpc.po.DeviceType;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DeviceServiceTest {
    @Resource
    DeviceService deviceService = new DeviceService();

    static DeviceParam deviceParam = new DeviceParam();
    static int deviceId = 1;
    static Assignee assignee = new Assignee();

    @BeforeAll
    static void setUpBeforeClass() {
        deviceParam = DeviceParam.builder()
                .id(1) // optional
                .name("Device Name")
                .position("Position")
                .type(DeviceType.SERVER) // replace with your actual enum value
                .clusterId(1) // e.g., 1 for 太乙, 2 for 启明
                .other(null) // replace with an actual JsonNode if needed
                .build();
        assignee = Assignee.builder().deviceId(1).userId(1).build();
    }

    @Test
    void all() {
        deviceService.all();
    }

    @Test
    void get() {
        deviceService.get(1);
    }

    @Test
    void add() {
        deviceService.add(deviceParam);
    }

    @Test
    void modify() {

        deviceService.modify(deviceId, deviceParam);
    }

    @Test
    void delete() {
        deviceService.delete(deviceId);
    }

    @Test
    void addAssignee() {
        deviceService.addAssignee(assignee);
    }

    @Test
    void deleteAssignee() {
        deviceService.deleteAssignee(assignee);
    }

    @Test
    void getAssignees() {
        deviceService.getAssignees(deviceId);
    }
}
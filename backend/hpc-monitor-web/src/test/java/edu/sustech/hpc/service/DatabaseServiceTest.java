package edu.sustech.hpc.service;

import edu.sustech.hpc.model.vo.HardwareInfo;
import edu.sustech.hpc.model.vo.ServerInfo;
import edu.sustech.hpc.po.HardwareType;
import edu.sustech.hpc.po.Server;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class DatabaseServiceTest {
    @Resource
    private DatabaseService databaseService;

    @Test
    public void getServerInfoTest() {
        System.out.println(databaseService.getServerInfoFromHardwareInfo(
                new HardwareInfo(HardwareType.BMC, "a", "b", 1, 2)
        ));
    }
}

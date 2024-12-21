package edu.sustech.hpc.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import edu.sustech.hpc.dao.*;
import edu.sustech.hpc.model.vo.*;
import edu.sustech.hpc.po.*;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DatabaseService {
    @Resource
    private ServerDao serverDao;

    @Resource
    private DeviceDao deviceDao;

    @Resource
    private ClusterDao clusterDao;

    ServerInfo getServerInfoFromHardwareInfo(HardwareInfo hardwareInfo) {
        return new ServerInfo(serverDao.selectOne(new LambdaQueryWrapper<Server>()
                .eq(Server::getId, hardwareInfo.getServerId())));
    }

    DeviceInfo getDeviceInfoFromServerInfo(ServerInfo serverInfo) {
        return new DeviceInfo(deviceDao.selectOne(new LambdaQueryWrapper<Device>()
                .eq(Device::getId, serverInfo.getId())));
    }

    public List<Cluster> getClusterInfo() {
        return clusterDao.selectList(null);
    }
}

package edu.sustech.hpc.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.yulichang.toolkit.JoinWrappers;
import edu.sustech.hpc.dao.AssigneeDao;
import edu.sustech.hpc.dao.DeviceAlertDao;
import edu.sustech.hpc.dao.DeviceDao;
import edu.sustech.hpc.dao.ServerDao;
import edu.sustech.hpc.dao.OtherDeviceAlertDao;
import edu.sustech.hpc.exceptions.DuplicateDeviceException;
import edu.sustech.hpc.model.dto.ServerHardwareBmc;
import edu.sustech.hpc.model.param.DeviceParam;
import edu.sustech.hpc.po.*;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static edu.sustech.hpc.po.DeviceType.SERVER;
import static edu.sustech.hpc.util.AssertUtil.asserts;

@Service
public class DeviceService {

    @Resource
    private DeviceDao deviceDao;
    @Resource
    private ServerDao serverDao;

    @Resource
    private AssigneeDao assigneeDao;

    @Resource
    private OtherDeviceAlertDao otherDeviceAlertDao;


    /**
     * @return 获取所有设备，不填入设备专有字段other
     */
    public List<DeviceParam> all() {
        return deviceDao.selectList(new LambdaQueryWrapper<Device>()
                        .eq(Device::getDeleted, false))
                .stream()
                .map(device -> {
                    DeviceParam deviceParam = new DeviceParam();
                    BeanUtil.copyProperties(device, deviceParam);
                    return deviceParam;
                })
                .toList();
    }

    /**
     * 查询单个设备，填入设备专有字段other
     *
     * @param id 设备id
     * @return 如果设备id不存在或已被删除，返回null
     */
    public DeviceParam get(Integer id) {
        Device device = deviceDao.selectById(id);
        if (device == null || device.getDeleted()) {
            return null;
        }
        DeviceParam deviceParam = new DeviceParam();
        BeanUtil.copyProperties(device, deviceParam);

        //获取设备专有字段other
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode other = mapper.createObjectNode();
        if (device.getType() == SERVER) {
            //查询server + hardware + bmc表
            ServerHardwareBmc deviceInfo =
                    serverDao.selectJoinOne(ServerHardwareBmc.class,
                            JoinWrappers.lambda(Server.class)
                                    .selectAll(Server.class)
                                    .selectAs(Hardware::getIp, ServerHardwareBmc::getBmcIp)
                                    .selectAll(Bmc.class)
                                    .leftJoin(Hardware.class, Hardware::getServerId, Server::getId)
                                    .leftJoin(Bmc.class, Bmc::getId, Hardware::getId)
                                    .eq(Server::getId, id));

            //填入other字段
            other.put("ip", deviceInfo.getIp())
                    .put("manageIp", deviceInfo.getManageIp())
                    .put("publicIp", deviceInfo.getPublicIp())
                    .set("bmc", mapper.createObjectNode()
                            .put("ip", deviceInfo.getBmcIp())
                            .put("user", deviceInfo.getUser())
                            .put("password", deviceInfo.getPassword())
                            .put("port", deviceInfo.getPort()));
        }
        else {
            OtherDeviceAlert otherDeviceAlert = otherDeviceAlertDao.selectById(id);
            if (otherDeviceAlert != null) {
                other.put("alert", otherDeviceAlert.isAlert());
            }
        }
        return deviceParam.setOther(other);
    }

    @Transactional
    public DeviceParam add(DeviceParam deviceParam) {
        Device device = new Device();
        BeanUtil.copyProperties(deviceParam, device);
        //avoid duplicate devices with the same names
        Device existingDevice = deviceDao.selectOne(
                new LambdaQueryWrapper<Device>()
                        .eq(Device::getName, deviceParam.getName())
        );
        if (existingDevice != null) {
            throw new DuplicateDeviceException("A device with the same name already exists: " + deviceParam.getName());
        }
        deviceDao.insert(device);
        if (device.getType() == SERVER) {
            JsonNode serverInfo = deviceParam.getOther();
            Server server = new Server()
                    .setId(device.getId())
                    .setIp(serverInfo.get("ip").asText())
                    .setManageIp(serverInfo.get("manageIp").asText())
                    .setPublicIp(serverInfo.get("publicIp").asText());
            serverDao.insert(server);
        }
        return deviceParam.setId(device.getId());
    }

    public DeviceParam modify(Integer id, DeviceParam deviceParam) {
        Device device = deviceDao.selectById(id);
        asserts(device != null && !device.getDeleted(), "设备ID不存在");
        BeanUtil.copyProperties(deviceParam, device, "id");
        deviceDao.updateById(device);
        if (device.getType() == SERVER) {
            JsonNode serverInfo = deviceParam.getOther();
            Server server = new Server()
                    .setId(id)
                    .setIp(serverInfo.get("ip").asText())
                    .setManageIp(serverInfo.get("manageIp").asText())
                    .setPublicIp(serverInfo.get("publicIp").asText());
            serverDao.updateById(server);
        }
        else {
            JsonNode otherDeviceAlert = deviceParam.getOther();
            OtherDeviceAlert otherDeviceAlert1 = new OtherDeviceAlert()
                    .setId(id)
                    .setAlert(otherDeviceAlert.get("alert").asBoolean());
            otherDeviceAlertDao.updateById(otherDeviceAlert1);
        }
        return deviceParam.setId(id);
    }

    public void delete(Integer id) {
        deviceDao.updateById(new Device()
                .setId(id)
                .setDeleted(true));
    }

    public void addAssignee(Assignee assignee) {
        Assignee assignee1 = assigneeDao.selectOne(new LambdaQueryWrapper<Assignee>()
                .eq(Assignee::getDeviceId, assignee.getDeviceId())
                .eq(Assignee::getUserId, assignee.getUserId()));
        if (assignee1 == null)
            assigneeDao.insert(assignee);
    }

    public void deleteAssignee(Assignee assignee) {
        assigneeDao.delete(
                new LambdaQueryWrapper<Assignee>()
                        .eq(Assignee::getDeviceId, assignee.getDeviceId())
                        .eq(Assignee::getUserId, assignee.getUserId())
        );
    }

    public List<Assignee> getAssignees(Integer deviceId) {
        LambdaQueryWrapper<Assignee> queryWrapper = new LambdaQueryWrapper<>();
        if (deviceId != null)
            queryWrapper.eq(Assignee::getDeviceId, deviceId);
        return assigneeDao.selectList(queryWrapper);
    }
}

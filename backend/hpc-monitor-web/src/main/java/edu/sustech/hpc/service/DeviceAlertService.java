package edu.sustech.hpc.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.yulichang.toolkit.JoinWrappers;
import edu.sustech.hpc.dao.DeviceAlertDao;
import edu.sustech.hpc.dao.DeviceDao;
import edu.sustech.hpc.model.param.DeviceAlertParam;
import edu.sustech.hpc.po.Device;
import edu.sustech.hpc.po.DeviceAlert;
import edu.sustech.hpc.result.PageResult;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class DeviceAlertService {

    @Resource
    private DeviceAlertDao deviceAlertDao;

    /**
     * 查询所有告警
     *
     * @return 告警参数列表
     */
    public List<DeviceAlertParam> all() {
        return deviceAlertDao.selectList(new LambdaQueryWrapper<DeviceAlert>()
                        .eq(DeviceAlert::getDeleted, false))
                .stream()
                .map(alert -> {
                    DeviceAlertParam alertParam = new DeviceAlertParam();
                    BeanUtil.copyProperties(alert, alertParam); // 转换为 Param
                    return alertParam;
                })
                .toList();
    }

    /**
     * 查询单个告警，补充设备信息
     *
     * @param id 告警 ID
     * @return 告警参数
     */
    public DeviceAlertParam get(Integer id) {
        DeviceAlert alert = deviceAlertDao.selectById(id);
        if (alert == null || alert.getDeleted()) {
            return null;
        }
        DeviceAlertParam alertParam = new DeviceAlertParam();
        BeanUtil.copyProperties(alert, alertParam);
        return alertParam;
    }

    @Transactional
    public DeviceAlertParam add(DeviceAlertParam alertParam) {
        DeviceAlert alert = new DeviceAlert();

        BeanUtil.copyProperties(alertParam, alert);

        deviceAlertDao.insert(alert);

        alertParam.setId(alert.getId());

        return alertParam;
    }
    @Transactional
    public DeviceAlertParam modify(Integer id, DeviceAlertParam alertParam) {
        DeviceAlert existingAlert = deviceAlertDao.selectById(id);
        if (existingAlert == null) {
            throw new IllegalArgumentException("告警记录不存在");
        }
        BeanUtil.copyProperties(alertParam, existingAlert);
        deviceAlertDao.updateById(existingAlert);
        return alertParam.setId(id);
    }

    public void delete(Integer id) {
        deviceAlertDao.deleteById(id);
    }

    /**
     * 根据设备ID查询告警
     * @param deviceId 设备ID
     * @return 根据设备ID查询到的告警列表
     */
    public List<DeviceAlertParam> getByDeviceId(Integer deviceId) {
        // 查询数据库中对应设备的告警记录
        List<DeviceAlert> alerts = deviceAlertDao.selectList(new LambdaQueryWrapper<DeviceAlert>()
                .eq(DeviceAlert::getDeviceId, deviceId)  // 通过 device_id 查询
                .eq(DeviceAlert::getDeleted, false)); // 查询未被删除的记录

        // 如果没有查询到告警信息，返回空列表
        if (alerts == null || alerts.isEmpty()) {
            return new ArrayList<>();
        }

        // 转换为 DeviceAlertParam 对象列表并返回
        return alerts.stream()
                .map(alert -> {
                    DeviceAlertParam alertParam = new DeviceAlertParam();
                    BeanUtil.copyProperties(alert, alertParam); // 转换为 Param
                    return alertParam;
                })
                .toList();
    }

}

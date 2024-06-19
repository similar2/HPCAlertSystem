package edu.sustech.hpc.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import edu.sustech.hpc.dao.*;
import edu.sustech.hpc.model.param.AlertParam;
import edu.sustech.hpc.model.vo.AlertRuleInfo;
import edu.sustech.hpc.model.vo.PrometheusAlertInfo;
import edu.sustech.hpc.po.*;
import edu.sustech.hpc.util.EmailUtil;
import jakarta.annotation.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static edu.sustech.hpc.util.AssertUtil.asserts;

@Service
public class AlertService {

    @Resource
    private AlertDao alertDao;
    @Resource
    private UserDao userDao;

    @Resource
    private EmailUtil emailUtil;

    @Resource
    private DeviceDao deviceDao;

    @Resource
    private AssigneeDao assigneeDao;

    @Resource
    private PrometheusAlertDao prometheusAlertDao;

    Alert getActiveAlert(String alertName, String deviceName) {
        return alertDao.selectOne(new LambdaQueryWrapper<Alert>()
                .eq(Alert::getDeviceName, deviceName)
                .eq(Alert::getAlertName, alertName)
                .isNull(Alert::getSolveTime));
    }

    public Alert add(AlertParam alertParam){
        Alert alert = new Alert()
                        .setDescription(alertParam.getDescription())
                        .setDeviceName(alertParam.getDeviceName())
                        .setAlertName(alertParam.getAlertName())
                        .setSeverity(alertParam.getSeverity());
        return add(alert);
    }

    public Alert add(Alert alert) {
        alertDao.insert(alert);
        notifyAssignees(alert);
        return alert;
    }

    public Alert solve(Integer id, String solveMethod){
        Alert alert = alertDao.selectById(id);
        asserts(alert!=null, "告警ID不存在");
        alert.setSolveTime(LocalDateTime.now());
        alert.setSolveMethod(solveMethod);
        alertDao.updateById(alert);
        return alert;
    }

    void addPrometheusAlert(Alert alert, PrometheusAlertInfo prometheusAlertInfo) {
        add(alert);
        prometheusAlertDao.insert(
                PrometheusAlert.builder()
                        .alertId(alert.getId())
                        .lastOccurrence(alert.getCreateTime())
                        .alertName(prometheusAlertInfo.getAlertName())
                        .ip(prometheusAlertInfo.getInstance())
                        .build()
        );
    }

    void updatePrometheusAlert(Alert alert, PrometheusAlertInfo prometheusAlertInfo) {
        PrometheusAlert prometheusAlert = prometheusAlertDao.selectById(alert.getId());
        if(prometheusAlertInfo.getActiveTime().isAfter(prometheusAlert.getLastOccurrence())) {
            prometheusAlert.setLastOccurrence(prometheusAlertInfo.getActiveTime());
            prometheusAlertDao.updateById(prometheusAlert);
        }
    }

    public Alert get(Integer id){
        return alertDao.selectById(id);
    }

    public List<Alert> getAll(
            String deviceName,
            LocalDateTime startTime,
            LocalDateTime endTime,
            Boolean solved){
        LambdaQueryWrapper<Alert> queryWrapper = new LambdaQueryWrapper<>();
        if(deviceName != null)
            queryWrapper.eq(Alert::getDeviceName, deviceName);
        if(startTime != null)
            queryWrapper.ge(Alert::getCreateTime, startTime);
        if(endTime != null)
            queryWrapper.le(Alert::getCreateTime, endTime);
        if(solved != null)
            if(solved)
                queryWrapper.isNotNull(Alert::getSolveTime);
            else
                queryWrapper.isNull(Alert::getSolveTime);
        return alertDao.selectList(queryWrapper);
    }

    public Alert modify(Integer id,
                        String description,
                        Integer dutyUserId,
                        String deviceName,
                        String solveMethod){
        Alert alert = alertDao.selectById(id);
        asserts(alert!=null, "告警ID不存在");
        if(dutyUserId!=null){ //检查责任人ID是否存在
            asserts(userDao.selectById(dutyUserId)!=null, "责任人ID不存在");
        }
        alert.setDescription(description)
             .setDeviceName(deviceName)
             .setSolveMethod(solveMethod);
        alertDao.updateById(alert);
        return alert;
    }

    public void notifyAssignees(Alert alert) {
        Device device = deviceDao.selectOne(
                new LambdaQueryWrapper<Device>()
                        .eq(Device::getName, alert.getDeviceName())
        );
        List<Assignee> assignees =
                assigneeDao.selectList(
                        new LambdaQueryWrapper<Assignee>()
                                .eq(Assignee::getDeviceId, device.getId())
                );
        List<User> assigneeUsers = new ArrayList<>();
        for(Assignee assignee : assignees) {
            assigneeUsers.add(
                    userDao.selectOne(
                            new LambdaQueryWrapper<User>()
                                    .eq(User::getId, assignee.getUserId())
                    )
            );
        }
        for(User assigneeUser : assigneeUsers) {
            notifyAssignee(alert, assigneeUser);
        }
    }

    public void notifyAssignee(Alert alert, User user) {
        emailUtil.sendMail(user.getEmail(), alert.getInfo(), alert.getSubject());
    }
}

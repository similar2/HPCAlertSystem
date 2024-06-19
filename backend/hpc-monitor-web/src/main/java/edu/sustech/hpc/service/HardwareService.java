package edu.sustech.hpc.service;

import cn.hutool.core.bean.BeanUtil;
import com.fasterxml.jackson.databind.JsonNode;
import edu.sustech.hpc.dao.BmcDao;
import edu.sustech.hpc.dao.HardwareDao;
import edu.sustech.hpc.model.param.HardwareParam;
import edu.sustech.hpc.model.vo.BmcInfo;
import edu.sustech.hpc.model.vo.HardwareInfo;
import edu.sustech.hpc.po.*;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HardwareService {
    @Resource
    private HardwareDao hardwareDao;
    @Resource
    private BmcDao bmcDao;
    @Resource
    private BmcService bmcService;

    @Transactional
    public HardwareParam add(HardwareParam hardwareParam) {
        Hardware hardware = new Hardware();
        BeanUtil.copyProperties(hardwareParam, hardware);
        hardwareDao.insert(hardware);
        JsonNode hardwareInfo = hardwareParam.getOther();
        if (hardware.getType() == HardwareType.BMC) {
            Bmc bmc = new Bmc()
                    .setId(hardware.getId())
                    .setUser(hardwareInfo.get("username").asText())
                    .setPassword(hardwareInfo.get("password").asText())
                    .setPort(hardwareInfo.get("port").asInt());
            bmcDao.insert(bmc);
            bmcService.addHardware(
                    new HardwareInfo(hardware),
                    new BmcInfo(bmc));
        }
        hardwareParam.setId(hardware.getId());
        return hardwareParam;
    }

    @Transactional
    public Integer delete(Integer hardwareId) {
        HardwareInfo hardwareInfo = new HardwareInfo(hardwareDao.selectById(hardwareId));
        if(hardwareInfo.getType() == HardwareType.BMC) {
            bmcService.removeTarget(hardwareInfo,
                    new BmcInfo(bmcDao.selectById(hardwareId)));
        }
        hardwareDao.deleteById(hardwareId);
        bmcDao.deleteById(hardwareId);
        return 0;
    }
}

package edu.sustech.hpc.service;

import cn.hutool.core.bean.BeanUtil;
import edu.sustech.hpc.dao.RoleDao;
import edu.sustech.hpc.model.vo.RoleVo;
import edu.sustech.hpc.po.Role;
import edu.sustech.hpc.result.ApiResponse;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Yuxian Wu
 * @version 1.0
 * @Description: TODO
 * @Create: 2024-10-23 11:28
 */
@Service
@Slf4j
public class RoleService {
    @Resource
    private RoleDao roleDao;

    public List<RoleVo> getRoleList() {
        List<Role> roles = roleDao.selectList(null);
        List<RoleVo> roleVoList = BeanUtil.copyToList(roles, RoleVo.class);
        return roleVoList;
    }
}

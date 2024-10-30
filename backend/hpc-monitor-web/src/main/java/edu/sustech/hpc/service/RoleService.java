package edu.sustech.hpc.service;

import cn.hutool.core.bean.BeanUtil;
import edu.sustech.hpc.dao.RoleDao;
import edu.sustech.hpc.model.dto.RoleDTO;
import edu.sustech.hpc.model.vo.RoleVo;
import edu.sustech.hpc.po.Role;
import edu.sustech.hpc.result.ApiResponse;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public RoleVo getRoleById(Long id) {
        Role role = roleDao.selectById(id);
        if (role == null) {
            log.error("Role with id {} not found", id);
            return null;
        }
        return BeanUtil.copyProperties(role, RoleVo.class);
    }

    public void createRole(RoleDTO roleDto) {
        Role role = BeanUtil.copyProperties(roleDto, Role.class);
        roleDao.insert(role);
        log.info("Role created: {}", role);
    }

    public void updateRole(Long id, RoleDTO roleDto) {
        Role existingRole = roleDao.selectById(id);
        if (existingRole == null) {
            log.error("Role with id {} not found", id);
            return;
        }
        BeanUtil.copyProperties(roleDto, existingRole, "id");
        roleDao.updateById(existingRole);
        log.info("Role updated: {}", existingRole);
    }

    public void deleteRole(Long id) {
        Role role = roleDao.selectById(id);
        if (role == null) {
            log.error("Role with id {} not found", id);
            return;
        }
        roleDao.deleteById(id);
        log.info("Role with id {} deleted", id);
    }
}

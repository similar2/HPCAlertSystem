package edu.sustech.hpc.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import edu.sustech.hpc.dao.PermissionDao;
import edu.sustech.hpc.dao.RoleDao;
import edu.sustech.hpc.model.vo.PermissionByGroupVo;
import edu.sustech.hpc.model.vo.PermissionVo;
import edu.sustech.hpc.po.Permission;
import edu.sustech.hpc.po.Role;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Yuxian Wu
 * @version 1.0
 * @Description: 权限管理服务
 * @Create: 2024-10-24 1:27
 */
@Service
@Slf4j
public class PermissionService {

    @Resource
    private PermissionDao permissionDao;

    @Resource
    private RoleDao roleDao;

    public List<PermissionByGroupVo> getAllPermissions() {
        List<PermissionByGroupVo> permissionByGroupVoList = new ArrayList<>();

        List<Permission> permissions = permissionDao.selectList(null);
        // menuName到 Permission的映射
        Map<String, List<Permission>> collect = permissions.stream()
                .collect(Collectors.groupingBy(Permission::getMenuName));


        CopyOptions options = CopyOptions.create().setFieldMapping(Map.of("requiredPermission", "requiredPerm"));
        for (String menuName : collect.keySet()) {
            permissionByGroupVoList.add(
                    PermissionByGroupVo.builder()
                            .menuName(menuName)
                            .permissions(BeanUtil.copyToList(collect.get(menuName), PermissionVo.class, options))
                            .build()
            );
        }

        return permissionByGroupVoList;
    }

    /**
     * 根据用户id获取权限信息
     * @param id
     * @return
     */
    public List<String> getPermissionsByUserId(Integer id) {
        List<Role> roles = roleDao.selectByUserId(id);

        Set<Permission> permissions = new HashSet<>();
        for (Role role : roles) {
            permissions.addAll(permissionDao.selectPermissionByRoleId(role.getId()));
        }

        return permissions.stream().map(Permission::getPermissionCode).collect(Collectors.toList());
    }
}

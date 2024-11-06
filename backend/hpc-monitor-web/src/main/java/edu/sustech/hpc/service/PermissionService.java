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

@Service
@Slf4j
public class PermissionService {

    @Resource
    private PermissionDao permissionDao;

    @Resource
    private RoleDao roleDao;

    /**
     * 获取所有权限并按菜单名称分组。
     *
     * @return List of grouped permissions by menu name
     */
    public List<PermissionByGroupVo> getAllPermissions() {
        List<PermissionByGroupVo> permissionByGroupVoList = new ArrayList<>();
        List<Permission> permissions = permissionDao.selectList(null);

        // 将权限按 menuName 分组
        Map<String, List<Permission>> groupedPermissions = permissions.stream()
                .collect(Collectors.groupingBy(Permission::getMenuName));

        CopyOptions options = CopyOptions.create().setFieldMapping(Map.of("requiredPermission", "requiredPerm"));
        for (String menuName : groupedPermissions.keySet()) {
            permissionByGroupVoList.add(
                    PermissionByGroupVo.builder()
                            .menuName(menuName)
                            .permissions(BeanUtil.copyToList(groupedPermissions.get(menuName), PermissionVo.class, options))
                            .build()
            );
        }
        return permissionByGroupVoList;
    }

    /**
     * 获取所有权限（不分组）。
     *
     * @return List of all permissions
     */
    public List<Permission> getAllPermissions_notGrouped() {
        return permissionDao.findAllPermissions();
    }

    /**
     * 根据权限 ID 获取权限信息
     *
     * @param id 权限 ID
     * @return Permission object
     */
    public Permission getPermissionById(Long id) {
        return permissionDao.findPermissionById(id);
    }

    /**
     * 更新权限信息
     *
     * @param id         权限 ID
     * @param permission 权限对象
     * @return 是否更新成功
     */
    public boolean updatePermission(Long id, Permission permission) {
        permission.setId(Math.toIntExact(id));
        return permissionDao.updatePermission(permission) > 0;
    }

    /**
     * 删除权限
     *
     * @param id 权限 ID
     * @return 是否删除成功
     */
    public boolean deletePermission(Long id) {
        return permissionDao.deletePermission(id) > 0;
    }

    /**
     * 创建新权限
     *
     * @param permission 权限对象
     * @return 是否创建成功
     */
    public boolean createPermission(Permission permission) {
        return permissionDao.insertPermission(permission) > 0;
    }

    /**
     * 根据用户 ID 获取权限代码列表
     *
     * @param id 用户 ID
     * @return List of permission codes
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

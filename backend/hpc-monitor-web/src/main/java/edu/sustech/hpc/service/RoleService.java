package edu.sustech.hpc.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import edu.sustech.hpc.dao.PermissionDao;
import edu.sustech.hpc.dao.RoleDao;
import edu.sustech.hpc.dao.RolePermissionDao;
import edu.sustech.hpc.dao.UserDao;
import edu.sustech.hpc.model.dto.RoleDTO;
import edu.sustech.hpc.model.vo.*;
import edu.sustech.hpc.po.Permission;
import edu.sustech.hpc.po.Role;
import edu.sustech.hpc.po.RolePermission;
import edu.sustech.hpc.po.User;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Resource
    private UserDao userDao;

    @Resource
    private PermissionDao permissionDao;

    @Resource
    private RolePermissionDao rolePermissionDao;


    /**
     * 获取角色列表(不包含权限)
     *
     * @return
     */
    public List<RoleVo> getRoleList() {
        List<Role> roles = roleDao.selectList(null);
        List<RoleVo> roleVoList = BeanUtil.copyToList(roles, RoleVo.class);
        return roleVoList;
    }

    /**
     * 根据角色名动态查询查询角色列表(包含每个角色的权限)
     *
     * @param roleName
     * @return
     */
    public List<RolePermissionVo> pageQuery(String roleName) {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(roleName)) {
            wrapper.like(Role::getRoleName, roleName);
        }
        List<Role> roles = roleDao.selectList(wrapper);
        // 映射Role的id字段到RolePermissionVo的roleId字段
        CopyOptions options = CopyOptions.create().setFieldMapping(Map.of("id", "roleId"));
        List<RolePermissionVo> roleVoList = BeanUtil.copyToList(roles, RolePermissionVo.class, options);

        for (RolePermissionVo rolePermissionVo : roleVoList) {
            // 查询角色对应的权限
            List<PermissionByGroupVo> permissionByGroupVoList = new ArrayList<>();

            List<Permission> permissions = permissionDao.selectPermissionByRoleId(rolePermissionVo.getRoleId());
            // menuCode到 Permission的映射
            Map<String, List<Permission>> collect = permissions.stream()
                    .collect(Collectors.groupingBy(Permission::getMenuCode));

            // menuCode 到 menuName 的映射
            Map<String, String> menuCodeToMenuNameMap = collect.entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            entry -> entry.getValue().getFirst().getMenuName()
                    ));

            // 映射Permission的 id字段到 PermissionVo的permissionId字段
            options = CopyOptions.create().setFieldMapping(Map.of("id", "permissionId"));
            for (String menuCode : menuCodeToMenuNameMap.keySet()) {
                permissionByGroupVoList.add(PermissionByGroupVo.builder()
                        .menuCode(menuCode)
                        .menuName(menuCodeToMenuNameMap.get(menuCode))
                        .permissions(BeanUtil.copyToList(collect.get(menuCode), PermissionVo.class, options))
                        .build());
            }
            rolePermissionVo.setMenus(permissionByGroupVoList);


            // 查询角色对应的用户
            List<User> users = userDao.selectUserByRoleId(rolePermissionVo.getRoleId());

            // 映射User的id字段到RoleUserVo的userId字段
            options = CopyOptions.create().setFieldMapping(Map.of("id", "userId"));
            List<RoleUserVo> roleUserVos = BeanUtil.copyToList(users, RoleUserVo.class, options);

            rolePermissionVo.setUsers(roleUserVos);
        }
        return roleVoList;
    }

    /**
     * 新增角色
     *
     * @param roleDTO
     */
    public void addRole(RoleDTO roleDTO) {
        // 新增角色
        Role role = BeanUtil.copyProperties(roleDTO, Role.class);
        role.setDeleted(0);
        role.setCreateTime(LocalDateTime.now());
        role.setUpdateTime(LocalDateTime.now());
        roleDao.insert(role);

        // 新增角色权限关联
        List<Integer> permissions = roleDTO.getPermissions();
        permissions.forEach(permissionId ->
                rolePermissionDao.insert(
                        RolePermission.builder()
                                .roleId(role.getId())
                                .permissionId(permissionId)
                                .build()
                ));
    }

    /**
     * 修改角色
     * @param roleDTO
     */
    public void updateRole(RoleDTO roleDTO) {
        // 映射RoleDTO的 roleId字段到Role的 id字段
        CopyOptions options = CopyOptions.create().setFieldMapping(Map.of("roleId", "id"));
        // 更新角色
        Role role = new Role();
        BeanUtil.copyProperties(roleDTO, role, options);
        role.setUpdateTime(LocalDateTime.now());
        roleDao.updateById(role);

        // 删除角色权限关联
        rolePermissionDao.delete(new LambdaQueryWrapper<RolePermission>().eq(RolePermission::getRoleId, role.getId()));

        // 新增角色权限关联
        List<Integer> permissions = roleDTO.getPermissions();
        permissions.forEach(permissionId ->
                rolePermissionDao.insert(
                        RolePermission.builder()
                                .roleId(role.getId())
                                .permissionId(permissionId)
                                .build()
                ));
    }

    public void deleteRole(Integer id) {
        roleDao.deleteById(id);
        rolePermissionDao.delete(new LambdaQueryWrapper<RolePermission>().eq(RolePermission::getRoleId, id));
    }
}

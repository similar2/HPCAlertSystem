package edu.sustech.hpc.dao;

import com.github.yulichang.base.MPJBaseMapper;
import edu.sustech.hpc.po.Permission;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface PermissionDao extends MPJBaseMapper<Permission> {

    /**
     * 查询所有权限
     */
    @Select("SELECT * FROM monitor.permission")
    List<Permission> findAllPermissions();

    /**
     * 根据权限ID查询权限信息
     */
    @Select("SELECT * FROM monitor.permission WHERE id = #{id}")
    Permission findPermissionById(Long id);

    /**
     * 更新权限信息
     */
    @Update("UPDATE monitor.permission SET menu_name = #{menuName}, permission_name = #{permissionName}, required_permission = #{requiredPermission} WHERE id = #{id}")
    int updatePermission(Permission permission);

    /**
     * 根据ID删除权限
     */
    @Delete("DELETE FROM monitor.permission WHERE id = #{id}")
    int deletePermission(Long id);

    /**
     * 插入新权限
     */
    /**
     * 插入新权限
     */
    @Insert("INSERT INTO monitor.permission (menu_code, menu_name, permission_name, permission_code, required_permission) VALUES (#{menuCode}, #{menuName}, #{permissionName}, #{permissionCode}, #{requiredPermission})")
    int insertPermission(Permission permission);


    /**
     * 根据角色ID查询相关权限
     */
    @Select("SELECT * FROM monitor.permission WHERE id IN (SELECT permission_id FROM monitor.role_permission WHERE role_id = #{roleId})")
    List<Permission> selectPermissionByRoleId(Integer roleId);
}

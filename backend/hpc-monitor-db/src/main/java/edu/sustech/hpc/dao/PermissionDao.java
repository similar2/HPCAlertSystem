package edu.sustech.hpc.dao;

import com.github.yulichang.base.MPJBaseMapper;
import edu.sustech.hpc.po.Permission;
import org.apache.ibatis.annotations.Select;

import java.util.List;

import org.apache.ibatis.annotations.*;

public interface PermissionDao extends MPJBaseMapper<Permission> {

    @Select("SELECT * FROM monitor.permission")
    List<Permission> findAllPermissions();

    @Select("SELECT * FROM monitor.permission WHERE id = #{id}")
    Permission findPermissionById(Long id);

    @Update("UPDATE monitor.permission SET menu_name = #{menuName}, permission_name = #{permissionName}, required_permission = #{requiredPermission} WHERE id = #{id}")
    int updatePermission(Permission permission);

    @Delete("DELETE FROM monitor.permission WHERE id = #{id}")
    int deletePermission(Long id);

    @Insert("INSERT INTO monitor.permission (menu_name, permission_name, required_permission) VALUES (#{menuName}, #{permissionName}, #{requiredPermission})")
    int insertPermission(Permission permission);
}


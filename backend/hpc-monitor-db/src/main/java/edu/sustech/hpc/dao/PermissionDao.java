package edu.sustech.hpc.dao;

import com.github.yulichang.base.MPJBaseMapper;
import edu.sustech.hpc.po.Permission;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface PermissionDao extends MPJBaseMapper<Permission> {

    @Select("select * from permission where id in (select permission_id from role_permission where role_id = #{roleId})")
    List<Permission> selectPermissionByRoleId(Integer roleId);
}

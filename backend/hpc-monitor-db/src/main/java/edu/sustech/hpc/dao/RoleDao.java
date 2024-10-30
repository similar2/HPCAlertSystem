package edu.sustech.hpc.dao;

import com.github.yulichang.base.MPJBaseMapper;
import edu.sustech.hpc.po.Role;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface RoleDao extends MPJBaseMapper<Role> {

    @Select("select * from role where id in (select role_id from user_role where user_id = #{id})")
    List<Role> selectByUserId(Integer id);
}

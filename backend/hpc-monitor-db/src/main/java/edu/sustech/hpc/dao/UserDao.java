package edu.sustech.hpc.dao;

import com.github.yulichang.base.MPJBaseMapper;
import edu.sustech.hpc.po.User;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface UserDao extends MPJBaseMapper<User> {

    @Select("select * from user where id in (select user_id from user_role where role_id = #{roleId})")
    List<User> selectUserByRoleId(Integer roleId);
}

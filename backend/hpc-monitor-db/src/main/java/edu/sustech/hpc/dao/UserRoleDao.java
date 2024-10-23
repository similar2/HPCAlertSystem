package edu.sustech.hpc.dao;

import com.github.yulichang.base.MPJBaseMapper;
import edu.sustech.hpc.po.User;
import edu.sustech.hpc.po.UserRole;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface UserRoleDao extends MPJBaseMapper<UserRole> {

}

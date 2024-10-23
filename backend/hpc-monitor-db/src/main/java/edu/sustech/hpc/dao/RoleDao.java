package edu.sustech.hpc.dao;

import com.github.yulichang.base.MPJBaseMapper;
import edu.sustech.hpc.po.Role;

import java.util.List;

public interface RoleDao extends MPJBaseMapper<Role> {
    List<Role> selectList();

    Role selectById(Long id);

    int insert(Role role);

    int updateById(Role role);

    void deleteById(Long id);
}

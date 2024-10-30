package edu.sustech.hpc.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Yuxian Wu
 * @version 1.0
 * @Description: 角色信息以及根据角色获取的所有该角色拥有的权限信息
 * @Create: 2024-10-23 10:13
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RolePermissionVo {
    private Integer roleId;
    private String roleName;

    private List<PermissionByGroupVo> menus;
    private List<RoleUserVo> users;
}

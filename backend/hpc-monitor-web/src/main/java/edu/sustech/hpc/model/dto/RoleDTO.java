package edu.sustech.hpc.model.dto;

import lombok.Data;

import java.util.List;

/**
 * @author Yuxian Wu
 * @version 1.0
 * @Description: 接收前端传来的角色信息
 * @Create: 2024-10-24 2:20
 */
@Data
public class RoleDTO {
    private Integer roleId;
    private String roleName;
    private List<Integer> permissions;
}

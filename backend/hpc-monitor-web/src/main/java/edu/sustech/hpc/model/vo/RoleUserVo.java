package edu.sustech.hpc.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Yuxian Wu
 * @version 1.0
 * @Description: 根据角色查询用户存储用户信息的类
 * @Create: 2024-10-23 23:50
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoleUserVo {
    private String name;
    private Integer userId;
}

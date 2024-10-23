package edu.sustech.hpc.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Yuxian Wu
 * @version 1.0
 * @Description: 传给前端的权限信息
 * @Create: 2024-10-24 1:08
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PermissionVo {
    private Integer permissionId;
    private String permissionName;

    private Integer id;
    private Integer requiredPerm;
}

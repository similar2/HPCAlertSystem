package edu.sustech.hpc.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Yuxian Wu
 * @version 1.0
 * @Description: 相同类别的权限信息
 * @Create: 2024-10-23 23:44
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PermissionByGroupVo {
    private String menuCode;
    private String menuName;
    private List<PermissionVo> permissions;
}

package edu.sustech.hpc.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Yuxian Wu
 * @version 1.0
 * @Description: TODO
 * @Create: 2024-10-23 10:13
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoleVo {
    private Integer id;
    private String roleName;
}

package edu.sustech.hpc.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author Yuxian Wu
 * @version 1.0
 * @Description: 权限实体类
 * @Create: 2024-10-23 23:38
 */
@Data
@Builder
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class Permission {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String menuCode;
    private String menuName;
    private String permissionCode;
    private String permissionName;
    private Integer requiredPermission;
}

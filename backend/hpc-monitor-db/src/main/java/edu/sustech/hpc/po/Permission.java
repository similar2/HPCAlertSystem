package edu.sustech.hpc.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Builder
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class Permission {
    @TableId(type = IdType.AUTO)
    private Integer id; // id

    private String menuCode; // 菜单代码
    private String menuName; // 菜单名称
    private String permissionCode; // 权限代码
    private String permissionName; // 权限名称

    private Integer requiredPermission;
}

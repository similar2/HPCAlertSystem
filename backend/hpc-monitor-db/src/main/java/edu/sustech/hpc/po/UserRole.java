package edu.sustech.hpc.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.experimental.Accessors;


@Data
@Accessors(chain = true)
public class UserRole {
    @TableId(type =  IdType.AUTO)
    private Integer id;
    private Integer userId;
    private Integer roleId;
}

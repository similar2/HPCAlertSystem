package edu.sustech.hpc.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class Role {
    @TableId(type= IdType.AUTO)
    private Integer id;
    private String roleName;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer deleted;
}

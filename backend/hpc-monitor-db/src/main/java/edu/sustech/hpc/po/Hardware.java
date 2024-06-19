package edu.sustech.hpc.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Hardware {
    @TableId(type=IdType.AUTO)
    private Integer id;
    private HardwareType type; //对应的硬件表名
    private String ip;
    private Integer serverId;
    private String name;
}

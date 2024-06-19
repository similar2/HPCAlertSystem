package edu.sustech.hpc.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Device {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private DeviceType type;
    private String name;
    private Integer clusterId;
    private String position;
    private Boolean deleted;
}

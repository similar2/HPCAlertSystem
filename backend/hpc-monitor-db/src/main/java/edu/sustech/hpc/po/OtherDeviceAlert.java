package edu.sustech.hpc.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class OtherDeviceAlert {

    @TableId
    private Integer id; // 设备记录的主键字段

    @TableField
    private boolean alert; // 警告状态
}

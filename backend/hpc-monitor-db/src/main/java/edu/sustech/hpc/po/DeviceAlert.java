package edu.sustech.hpc.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DeviceAlert {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer deviceId;
    private LocalDateTime alertTime;
    private LocalDateTime resolveTime;
    private String resolveMethod;
    private String alertStatus;
    private String alertLevel;
    private String description;
    private Boolean deleted;
    private String responsiblePerson;
}

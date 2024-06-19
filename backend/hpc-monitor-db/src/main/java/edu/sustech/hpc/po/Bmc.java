package edu.sustech.hpc.po;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 记录使用IPMI连接BMC的参数
 */
@Data
@Accessors(chain = true)
public class Bmc {
    @TableId
    private Integer id;
    private String user;
    private String password;
    private Integer port; //IPMI端口
}

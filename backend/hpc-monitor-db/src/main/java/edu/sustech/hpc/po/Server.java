package edu.sustech.hpc.po;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Server {
    @TableId
    private Integer id;
    private String ip; //校园网IP
    private String manageIp; //管理网IP
    private String publicIp; //公网IP
}

package edu.sustech.hpc.model.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ServerHardwareBmc {
    //server表
    private String ip; //校园网IP
    private String manageIp; //管理网IP
    private String publicIp; //公网IP

    //hardware表
    private String bmcIp;

    //bmc表
    private String user; //IPMI用户名
    private String password; //IPMI密码
    private String port; //IPMI端口
}

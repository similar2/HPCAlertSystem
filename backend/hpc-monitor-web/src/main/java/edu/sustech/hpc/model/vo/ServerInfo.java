package edu.sustech.hpc.model.vo;

import edu.sustech.hpc.po.Server;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServerInfo {
    private String ip, manageIp, publicIp;
    private Integer id;

    public ServerInfo(Server server) {
        if (server != null) {
            ip = server.getIp();
            manageIp = server.getManageIp();
            publicIp = server.getPublicIp();
            id = server.getId();
        }
    }
}

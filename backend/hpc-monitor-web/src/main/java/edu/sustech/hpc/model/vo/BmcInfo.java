package edu.sustech.hpc.model.vo;

import edu.sustech.hpc.po.Bmc;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BmcInfo {
    Integer id;
    String user, password;
    Integer port;

    public BmcInfo(Bmc bmc) {
        id = bmc.getId();
        user = bmc.getUser();
        password = bmc.getPassword();
        port = bmc.getPort();
    }
}

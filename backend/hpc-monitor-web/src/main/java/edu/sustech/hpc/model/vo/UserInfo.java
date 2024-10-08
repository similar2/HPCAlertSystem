package edu.sustech.hpc.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserInfo {
    private String token;
    private Integer id;
    private String name;
    private String email;
    private String phone;
    private Integer role;// 1 - engineer, 2 - system admin, 3 - super admin
    private Integer status; // 0 - inactive, 1 - active
}

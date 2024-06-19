package edu.sustech.hpc.model.vo;

import lombok.Data;
import lombok.experimental.Accessors;

//脱敏后的用户信息
@Data
@Accessors(chain = true)
public class PublicUserInfo {
    private Integer id; //用户ID
    private String username;
    private String email;
}

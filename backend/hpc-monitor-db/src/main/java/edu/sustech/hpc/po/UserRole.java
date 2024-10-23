package edu.sustech.hpc.po;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserWithRole {
    private Integer userId;
    private Integer roleId;
}

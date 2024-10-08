package edu.sustech.hpc.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Builder
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @TableId(type=IdType.AUTO)
    private Integer id;
    private String name;
    private String password; //BCrypt hashed password
    private String phone;
    private String email;
    private Integer role;// 1 - engineer, 2 - system admin, 3 - super admin
    private Integer status; // 0 - inactive, 1 - active
}

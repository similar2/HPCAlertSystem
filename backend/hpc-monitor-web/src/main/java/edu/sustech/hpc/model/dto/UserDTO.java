package edu.sustech.hpc.model.dto;

import lombok.Data;

/**
 * @author Yuxian Wu
 * @version 1.0
 * @Description: TODO
 * @Create: 2024-10-09 2:23
 */
@Data
public class UserDTO {
    private Integer id;

    private String name;

    private String password;

    private String phone;

    private String email;

    private Integer role;
}

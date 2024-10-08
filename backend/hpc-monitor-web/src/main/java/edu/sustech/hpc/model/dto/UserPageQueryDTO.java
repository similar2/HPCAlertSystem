package edu.sustech.hpc.model.dto;

import lombok.Data;

/**
 * @author Yuxian Wu
 * @version 1.0
 * @Description:
 * @Create: 2024-10-08 23:42
 */
@Data
public class UserPageQueryDTO {
    private String name;
    private String email;
    private String phone;
    private int page;
    private int pageSize;
    private int role;
}

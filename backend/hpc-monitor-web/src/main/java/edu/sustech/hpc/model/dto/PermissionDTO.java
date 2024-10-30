package edu.sustech.hpc.model.dto;

import lombok.Data;

@Data
public class PermissionDTO {
    private Integer id;
    private String menu_code;
    private String menu_name;
    private String permission_code;
    private String permission_name;
    private int required_permission;
}

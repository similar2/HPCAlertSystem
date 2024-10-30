package edu.sustech.hpc.controller;

import edu.sustech.hpc.po.Permission;
import edu.sustech.hpc.result.ApiResponse;
import edu.sustech.hpc.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    @GetMapping("/permissions")
    public ApiResponse<List<Permission>> getPermissions() {
        List<Permission> permissions = permissionService.getAllPermissions();
        return ApiResponse.success(permissions);
    }

    @GetMapping("/permissions/{id}")
    public ApiResponse<Permission> getPermissionById(@PathVariable Long id) {
        Permission permission = permissionService.getPermissionById(id);
        return ApiResponse.success(permission);
    }

    @PutMapping("/permissions/{id}")
    public ApiResponse<String> updatePermission(@PathVariable Long id, @RequestBody Permission permission) {
        boolean success = permissionService.updatePermission(id, permission);
        return ApiResponse.success("权限更新成功");
    }

    @DeleteMapping("/permissions/{id}")
    public ApiResponse<String> deletePermission(@PathVariable Long id) {
        boolean success = permissionService.deletePermission(id);
        return ApiResponse.success("权限删除成功") ;
    }

    @PostMapping("/permissions")
    public ApiResponse<String> createPermission(@RequestBody Permission permission) {
        boolean success = permissionService.createPermission(permission);
        return ApiResponse.success("权限创建成功");
    }
}
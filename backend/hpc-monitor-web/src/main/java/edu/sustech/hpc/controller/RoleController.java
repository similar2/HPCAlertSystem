package edu.sustech.hpc.controller;

import edu.sustech.hpc.model.dto.RoleDTO;
import edu.sustech.hpc.model.vo.RoleVo;
import edu.sustech.hpc.result.ApiResponse;
import edu.sustech.hpc.service.RoleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    // 获取角色列表
    @GetMapping // 这里使用 GET 方法
    public ApiResponse<List<RoleVo>> getRoleList() {
        List<RoleVo> roleVoList = roleService.getRoleList();
        return ApiResponse.success(roleVoList);
    }

    // 根据 ID 获取角色
    @GetMapping("/{id}") // 这里也使用 GET 方法
    public ApiResponse<RoleVo> getRoleById(@PathVariable Long id) {
        RoleVo roleVo = roleService.getRoleById(id);
        return ApiResponse.success(roleVo);
    }

    // 创建角色
    @PostMapping // 使用 POST 方法
    public ApiResponse createRole(@RequestBody RoleDTO roleDto) {
        roleService.createRole(roleDto);
        return ApiResponse.success();
    }

    // 更新角色信息
    @PutMapping("/{id}") // 使用 PUT 方法
    public ApiResponse updateRole(@PathVariable Long id, @RequestBody RoleDTO roleDto) {
        roleService.updateRole(id, roleDto);
        return ApiResponse.success();
    }

    // 删除角色
    @DeleteMapping("/{id}") // 使用 DELETE 方法
    public ApiResponse deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ApiResponse.success();
    }
}

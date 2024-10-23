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

    @GetMapping
    public ApiResponse<List<RoleVo>> getRoleList() {
        List<RoleVo> roleVoList = roleService.getRoleList();
        return ApiResponse.success(roleVoList);
    }

    @GetMapping("/{id}")
    public ApiResponse<RoleVo> getRoleById(@PathVariable Long id) {
        RoleVo roleVo = roleService.getRoleById(id);
        if (roleVo == null) {
            return ApiResponse.error("Role not found");
        }
        return ApiResponse.success(roleVo);
    }

    @PostMapping
    public ApiResponse createRole(@RequestBody RoleDTO roleDto) {
        roleService.createRole(roleDto);
        return ApiResponse.success();
    }

    @PutMapping("/{id}")
    public ApiResponse updateRole(@PathVariable Long id, @RequestBody RoleDTO roleDto) {
        roleService.updateRole(id, roleDto);
        return ApiResponse.success();
    }

    // 5. 删除角色
    @DeleteMapping("/{id}")
    public ApiResponse deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ApiResponse.success();
    }
}

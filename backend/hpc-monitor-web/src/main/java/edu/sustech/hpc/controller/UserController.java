package edu.sustech.hpc.controller;

import edu.sustech.hpc.annotation.PathController;
import edu.sustech.hpc.model.dto.RoleDTO;
import edu.sustech.hpc.model.dto.UserDTO;
import edu.sustech.hpc.model.dto.UserPageQueryDTO;
import edu.sustech.hpc.model.param.LoginParam;
import edu.sustech.hpc.model.param.RegisterParam;
import edu.sustech.hpc.model.vo.*;
import edu.sustech.hpc.po.UserRole;
import edu.sustech.hpc.result.ApiResponse;
import edu.sustech.hpc.result.PageResult;
import edu.sustech.hpc.service.PermissionService;
import edu.sustech.hpc.service.RoleService;
import edu.sustech.hpc.service.UserService;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@PathController("api/user")
public class UserController {
    @Resource
    private UserService userService;

    @Resource
    private RoleService roleService;

    @Resource
    private PermissionService permissionService;

    //获取所有脱敏后的用户信息
    @GetMapping("/all")
    public ApiResponse<List<PublicUserInfo>> all(@RequestParam(name = "user_id", required = false) Integer userId) {
        return ApiResponse.success(userService.all(userId));
    }

    @PostMapping("/send-verify-code")
    public ApiResponse sendVerifyCode(@Email @NotEmpty @RequestParam String email) {
        userService.sendVerifyCode(email);
        return ApiResponse.success();
    }

    @PostMapping("/register")
    public ApiResponse<UserInfo> register(@Validated @RequestBody RegisterParam registerParam) {
        UserInfo userInfo = userService.register(registerParam.getVerifyCode(),
                registerParam.getEmail(),
                registerParam.getUsername(),
                registerParam.getPassword());
        return ApiResponse.success(userInfo);
    }

    @PostMapping("/login")
    public ApiResponse<UserInfo> login(@Validated @RequestBody LoginParam loginParam) {
        UserInfo userInfo = userService.login(loginParam.getEmail(), loginParam.getPassword());
        return ApiResponse.success(userInfo);
    }

    /**
     * 分页查询
     *
     * @param userPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    public ApiResponse<PageResult> page(UserPageQueryDTO userPageQueryDTO) {
        PageResult pageResult = userService.pageQuery(userPageQueryDTO);
        return ApiResponse.success(pageResult);
    }

    /**
     * 启用或禁用账号
     *
     * @param status
     * @param id
     */
    @PostMapping("/status/{status}")
    public ApiResponse startOrStop(@PathVariable Integer status, Integer id) {
        userService.startOrStop(status, id);
        return ApiResponse.success();
    }

    /**
     * 新增用户
     *
     * @param userDTO
     * @return
     */
    @PostMapping
    public ApiResponse save(@RequestBody UserDTO userDTO) {
        userService.save(userDTO);
        return ApiResponse.success();
    }

    /**
     * 根据id查询用户信息
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ApiResponse<UserInfo> getById(@PathVariable Integer id) {
        return ApiResponse.success(userService.getById(id));
    }

    /**
     * 修改用户信息
     *
     * @param userDTO
     * @return
     */
    @PostMapping("/update")
    public ApiResponse update(@RequestBody UserDTO userDTO) {
        userService.update(userDTO);
        return ApiResponse.success();
    }

    /**
     * 通过邮箱获取用户信息
     *
     * @param email
     * @return
     */
    @GetMapping("/getByEmail/{email}")
    public ApiResponse<UserInfo> getByEmail(@PathVariable String email) {
        return ApiResponse.success(userService.getByEmail(email));
    }

    /**
     * 删除用户
     *
     * @param id
     * @return
     */
    @PostMapping("/{id}")
    public ApiResponse delete(@PathVariable Integer id) {
        userService.delete(id);
        return ApiResponse.success();
    }

    /**
     * 获取角色列表
     * @return
     */
    @GetMapping("/roleList")
    public ApiResponse<List<RoleVo>> getRoleList(){
        List<RoleVo> roleVoList = roleService.getRoleList();
        return ApiResponse.success(roleVoList);
    }

    /**
     * 分页查询角色
     * @param roleName
     * @return
     */
    @GetMapping("/rolePageQuery")
    public ApiResponse<List<RolePermissionVo>> rolePageQuery(String roleName){
        List<RolePermissionVo> roleVoList = roleService.pageQuery(roleName);
        return ApiResponse.success(roleVoList);
    }


    /**
     * 获取所有权限
     *
     * @return
     */
    @GetMapping("/getAllPermissions")
    public ApiResponse<List<PermissionByGroupVo>> getAllPermissions() {
        List<PermissionByGroupVo> permissionByGroupVoList = permissionService.getAllPermissions();
        return ApiResponse.success(permissionByGroupVoList);
    }

    /**
     * 新增角色
     * @param roleDTO
     * @return
     */
    @PostMapping("/addRole")
    public ApiResponse addRole(@RequestBody RoleDTO roleDTO) {
        roleService.addRole(roleDTO);
        return ApiResponse.success();
    }

    /**
     * 修改角色
     * @param roleDTO
     * @return
     */
    @PostMapping("/updateRole")
    public ApiResponse updateRole(@RequestBody RoleDTO roleDTO) {
        roleService.updateRole(roleDTO);
        return ApiResponse.success();
    }

    /**
     * 删除角色
     * @param id
     * @return
     */
    @PostMapping("/deleteRole/{id}")
    public ApiResponse deleteRole(@PathVariable Integer id) {
        roleService.deleteRole(id);
        return ApiResponse.success();
    }

    /**
     * 根据用户id获取权限信息
     * @param id
     * @return
     */
    @GetMapping("/permissions/{id}")
    public ApiResponse<List<String>> getPermissionsByUserId(@PathVariable Integer id) {
        return ApiResponse.success(permissionService.getPermissionsByUserId(id));
    }

    /**
     * 移除用户角色关系
     * @param userRole
     */
    @PostMapping("/removeUserRole")
    public ApiResponse removeUserRole(@RequestBody UserRole userRole){
        userService.removeUserRole(userRole);
        return ApiResponse.success();
    }

}

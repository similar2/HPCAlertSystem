package edu.sustech.hpc.controller;

import edu.sustech.hpc.annotation.PathController;
import edu.sustech.hpc.model.dto.UserDTO;
import edu.sustech.hpc.model.dto.UserPageQueryDTO;
import edu.sustech.hpc.model.param.LoginParam;
import edu.sustech.hpc.model.param.RegisterParam;
import edu.sustech.hpc.result.ApiResponse;
import edu.sustech.hpc.model.vo.PublicUserInfo;
import edu.sustech.hpc.model.vo.UserInfo;
import edu.sustech.hpc.result.PageResult;
import edu.sustech.hpc.service.UserService;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@PathController("/api/user")
public class UserController {
    @Resource
    private UserService userService;

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
//        System.out.println("login");
//        return ApiResponse.success(UserInfo.builder()
//                .token("token")
//                .id(1)
//                .name("name")
//                .email(loginParam.getEmail())
//                .role(3)
//                .build());
    }

    /**
     * 分页查询
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
     * @param status
     * @param id
     */
    @PostMapping("/status/{status}")
    public ApiResponse startOrStop(@PathVariable Integer status, Integer id){
        userService.startOrStop(status, id);
        return ApiResponse.success();
    }

    /**
     * 新增用户
     * @param userDTO
     * @return
     */
    @PostMapping
    public ApiResponse save(@RequestBody UserDTO userDTO){
        userService.save(userDTO);
        return ApiResponse.success();
    }

    @GetMapping("/{id}")
    public ApiResponse<UserInfo> getById(@PathVariable Integer id) {
        return ApiResponse.success(userService.getById(id));
    }

    @PutMapping
    public ApiResponse update(@RequestBody UserDTO userDTO) {
        userService.update(userDTO);
        return ApiResponse.success();
    }

    @GetMapping("/getByEmail/{email}")
    public ApiResponse<UserInfo> getByEmail(@PathVariable String email) {
        return ApiResponse.success(userService.getByEmail(email));
    }

    @DeleteMapping("/{id}")
    public ApiResponse delete(@PathVariable Integer id) {
        userService.delete(id);
        return ApiResponse.success();
    }
}

package edu.sustech.hpc.controller;

import edu.sustech.hpc.annotation.PathController;
import edu.sustech.hpc.model.param.LoginParam;
import edu.sustech.hpc.model.param.RegisterParam;
import edu.sustech.hpc.model.vo.ApiResponse;
import edu.sustech.hpc.model.vo.PublicUserInfo;
import edu.sustech.hpc.model.vo.UserInfo;
import edu.sustech.hpc.service.UserService;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@PathController("/api/user")
public class UserController {
    @Resource
    private UserService userService;

    //获取所有脱敏后的用户信息
    @GetMapping("/all")
    public ApiResponse<List<PublicUserInfo>> all(@RequestParam(name="user_id", required = false) Integer userId){
        return ApiResponse.success(userService.all(userId));
    }

    @PostMapping("/send-verify-code")
    public ApiResponse sendVerifyCode(@Email @NotEmpty @RequestParam String email){
        userService.sendVerifyCode(email);
        return ApiResponse.success();
    }

    @PostMapping("/register")
    public ApiResponse<UserInfo> register(@Validated @RequestBody RegisterParam registerParam){
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
}

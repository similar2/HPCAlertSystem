package edu.sustech.hpc.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import edu.sustech.hpc.dao.RoleDao;
import edu.sustech.hpc.dao.UserDao;
import edu.sustech.hpc.dao.UserRoleDao;
import edu.sustech.hpc.model.dto.UserDTO;
import edu.sustech.hpc.model.dto.UserPageQueryDTO;
import edu.sustech.hpc.model.vo.PublicUserInfo;
import edu.sustech.hpc.model.vo.RoleVo;
import edu.sustech.hpc.model.vo.UserInfo;
import edu.sustech.hpc.model.vo.UserPageQueryVo;
import edu.sustech.hpc.po.Role;
import edu.sustech.hpc.po.User;
import edu.sustech.hpc.po.UserRole;
import edu.sustech.hpc.result.PageResult;
import edu.sustech.hpc.util.EmailUtil;
import edu.sustech.hpc.util.JwtUtil;
import jakarta.annotation.Resource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static edu.sustech.hpc.util.AssertUtil.asserts;

@Service
public class UserService {

    public static final Logger LOGGER = LogManager.getLogger(UserService.class);

    @Resource
    private EmailUtil emailUtil;
    @Resource
    private ThreadPoolExecutor threadPool;

    //邮箱->验证码, 例:example@gmail.com->648413
    private final ConcurrentHashMap<String, String> verifyCodes = new ConcurrentHashMap<>();

    public void sendVerifyCode(String email) {
        //1.发送验证码
        String verifyCode = RandomUtil.randomNumbers(6); // 生成6位数字验证码
        String content = "验证码:" + verifyCode + ", 有效期2分钟";
        //2.验证码存入HashMap中，2分钟后清除
        threadPool.execute(() -> {
            emailUtil.sendMail(email, content, "SUSTech-HPC-Monitor邮箱验证");
            verifyCodes.put(email, verifyCode);
            LOGGER.info("Verify code {} sent to {}", verifyCode, email);
            try {
                Thread.sleep(TimeUnit.MINUTES.toMillis(2));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            verifyCodes.remove(email);
            LOGGER.info("Verify code {} of {} expired", verifyCode, email);
        });
    }

    @Resource
    private UserDao userDao;
    @Resource
    private UserRoleDao userRoleDao;
    @Resource
    private RoleDao roleDao;
    @Resource
    private PasswordEncoder passwordEncoder;

    //获取所有脱敏后的用户信息
    public List<PublicUserInfo> all(Integer userId) {
        List<User> users = userId == null ?
                userDao.selectList(null) :
                List.of(userDao.selectById(userId));//查询所有用户信息
        return users.stream()
                .map(user -> new PublicUserInfo()
                        .setId(user.getId())
                        .setUsername(user.getName())
                        .setEmail(user.getEmail()))
                .collect(Collectors.toList());
    }

    public UserInfo register(String verifyCode, String email, String username, String password) {
        String trueCode = verifyCodes.get(email);
        asserts(trueCode != null, "验证码已过期，请重新发送");
        asserts(trueCode.equals(verifyCode), "验证码错误");
        //判断该email是否已被注册
        User user = userDao.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getEmail, email));
        asserts(user == null, "该邮箱已被注册");

        //创建用户
        user = new User()
                .setName(username)
                .setPassword(passwordEncoder.encode(password))
                .setEmail(email);
        userDao.insert(user);
        return authenticate(user);
    }

    private UserInfo authenticate(User user) {
        //直接认证通过，就不经过AuthenticationManager#authenticate了
        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getId(), user.getPassword(), null);
        SecurityContextHolder.getContext().setAuthentication(authentication); //存入SecurityContext
        String jwt = JwtUtil.createJwt(String.valueOf(user.getId()));//使用id生成JWT返回
        UserInfo userInfo = new UserInfo();
        BeanUtils.copyProperties(user, userInfo);
        userInfo.setToken(jwt);
        return userInfo;
    }

    public UserInfo login(String email, String password) {
        User user = userDao.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getEmail, email));
        asserts(user != null, "用户不存在，请先注册");
        asserts(passwordEncoder.matches(password, user.getPassword()), "密码错误");
        asserts(user.getStatus() != 0, "账号已被锁定，请联系管理员");
        return authenticate(user);
    }

    /**
     * 分页查询
     * @param userPageQueryDTO
     * @return
     */
    public PageResult pageQuery(UserPageQueryDTO userPageQueryDTO) {
        // 创建分页对象
        Page<User> page = new Page<>(userPageQueryDTO.getPageNum(), userPageQueryDTO.getPageSize());

        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        if (userPageQueryDTO.getName() != null && !userPageQueryDTO.getName().isEmpty()) {
            queryWrapper.like(User::getName, userPageQueryDTO.getName());
        }
        if (userPageQueryDTO.getEmail() != null && !userPageQueryDTO.getEmail().isEmpty()) {
            queryWrapper.like(User::getEmail, userPageQueryDTO.getEmail());
        }
        if (userPageQueryDTO.getPhone() != null && !userPageQueryDTO.getPhone().isEmpty()) {
            queryWrapper.like(User::getPhone, userPageQueryDTO.getPhone());
        }
        Page<User> userPage = userDao.selectPage(page, queryWrapper);

        List<UserPageQueryVo> records = BeanUtil.copyToList(userPage.getRecords(), UserPageQueryVo.class);

        // 查出所有的角色
        List<Role> roles = roleDao.selectList(null);
        Map<Integer, String> roleMap = roles.stream()
                .collect(Collectors.toMap(
                        Role::getId,
                        Role::getRoleName
                ));

        for (UserPageQueryVo userPageQueryVo : records) {
            List<UserRole> userRoles = userRoleDao.selectList(new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId, userPageQueryVo.getId()));

            userPageQueryVo.setRoles( userRoles.stream()
                    .map(userRole -> {
                        Integer roleId = userRole.getRoleId();
                        String roleName = roleMap.get(roleId);
                        return RoleVo.builder()
                                .id(roleId)
                                .roleName(roleName)
                                .build();
                    })
                    .toList());
        }

        return new PageResult(userPage.getTotal(), records);
    }

    /**
     * 启用或禁用账号
     * @param status
     * @param id
     */
    public void startOrStop(Integer status, Integer id) {
        User user = User.builder()
                .id(id)
                .status(status)
                .build();
        userDao.updateById(user);
    }

    /**
     * 新增用户
     * @param userDTO
     */
    public void save(UserDTO userDTO) {
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        user.setStatus(1);
        if (userDTO.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        userDao.insert(user);

        List<Integer> roleIds = userDTO.getRoleIds();
        roleIds.forEach(roleId -> userRoleDao.insert(
                UserRole.builder()
                        .userId(user.getId())
                        .roleId(roleId)
                        .build()
        ));
    }

    /**
     * 根据id查询用户信息
     * @param id
     * @return
     */
    public UserInfo getById(Integer id) {
        User user = userDao.selectById(id);
        user.setPassword("*****");
        UserInfo userInfo = new UserInfo();
        BeanUtils.copyProperties(user, userInfo);
        return userInfo;
    }

    /**
     * 修改用户信息
     * @param userDTO
     */
    public void update(UserDTO userDTO) {
        User user = new User();
        BeanUtils.copyProperties(userDTO, user, "password");
        if (StrUtil.isNotBlank(userDTO.getPassword())) {
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }
        userDao.updateById(user);

        List<Integer> roleIds = userDTO.getRoleIds();
        userRoleDao.delete(new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId, user.getId()));
        roleIds.forEach(roleId -> userRoleDao.insert(
                UserRole.builder()
                        .userId(user.getId())
                        .roleId(roleId)
                        .build()
        ));
    }

    /**
     * 通过邮箱获取用户信息
     * @param email
     * @return
     */
    public UserInfo getByEmail(String email) {
        User user = userDao.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getEmail, email));
        UserInfo userInfo = new UserInfo();
        if (user != null) {
            BeanUtils.copyProperties(user, userInfo);
        }
        return userInfo;
    }

    /**
     * 删除用户
     * @param id
     */
    public void delete(Integer id) {
        userDao.deleteById(id);
        userRoleDao.delete(new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId, id));
    }

    /**
     * 移除用户角色关系
     * @param userRole
     */
    public void removeUserRole(UserRole userRole) {
        userRoleDao.delete(new LambdaQueryWrapper<UserRole>()
                .eq(UserRole::getUserId, userRole.getUserId())
                .eq(UserRole::getRoleId, userRole.getRoleId()));
    }
}

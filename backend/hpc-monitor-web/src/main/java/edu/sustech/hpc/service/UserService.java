package edu.sustech.hpc.service;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import edu.sustech.hpc.dao.UserDao;
import edu.sustech.hpc.model.vo.PublicUserInfo;
import edu.sustech.hpc.model.vo.UserInfo;
import edu.sustech.hpc.po.User;
import edu.sustech.hpc.util.EmailUtil;
import edu.sustech.hpc.util.JwtUtil;
import jakarta.annotation.Resource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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
	private final ConcurrentHashMap<String,String> verifyCodes = new ConcurrentHashMap<>();

	public void sendVerifyCode(String email) {
		//1.发送验证码
		String verifyCode = RandomUtil.randomNumbers(6); // 生成6位数字验证码
		String content = "验证码:"+ verifyCode +", 有效期2分钟";
		//2.验证码存入HashMap中，2分钟后清除
		threadPool.execute(()->{
			emailUtil.sendMail(email,content,"SUSTech-HPC-Monitor邮箱验证");
			verifyCodes.put(email,verifyCode);
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
	private PasswordEncoder passwordEncoder;

	//获取所有脱敏后的用户信息
	public List<PublicUserInfo> all(Integer userId){
		List<User> users = userId == null ?
				userDao.selectList(null) :
				List.of(userDao.selectById(userId));//查询所有用户信息
		return  users.stream()
					.map(user->new PublicUserInfo()
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
		asserts(user==null, "该邮箱已被注册");

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
		return new UserInfo(jwt, user.getId(), user.getName(), user.getEmail());
	}

	public UserInfo login(String email, String password) {
		User user = userDao.selectOne(new LambdaQueryWrapper<User>()
										 .eq(User::getEmail, email));
		asserts(user != null, "用户不存在，请先注册");
		asserts(passwordEncoder.matches(password, user.getPassword()), "密码错误");
		return authenticate(user);
	}
}

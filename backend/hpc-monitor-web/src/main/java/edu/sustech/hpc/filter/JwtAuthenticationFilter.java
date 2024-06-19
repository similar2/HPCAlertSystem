package edu.sustech.hpc.filter;

import edu.sustech.hpc.dao.UserDao;
import edu.sustech.hpc.po.User;
import edu.sustech.hpc.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Resource
    private UserDao userDao;

	@Override
	protected void doFilterInternal(@NotNull HttpServletRequest request,
	                                @NotNull HttpServletResponse response,
	                                @NotNull FilterChain filterChain) throws ServletException, IOException {
		//每次请求都是一个独立的SecurityContext
		String token = request.getHeader("token");
		if (StringUtils.hasText(token)){
			Claims claims = JwtUtil.parseJwt(token);
			String id = claims.getSubject();
			User user = userDao.selectById(id);
			if (user != null) { //如果MySQL中没有User，则SecurityContext中没有Authentication对象
				Authentication authentication = new UsernamePasswordAuthenticationToken(user.getId(), user.getPassword(), null);
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		}
		filterChain.doFilter(request, response); //没有user也直接放行，之后会被Interceptor拦截，因为SecurityContext中没有Authentication对象
	}
}

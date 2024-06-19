package edu.sustech.hpc.config;

import edu.sustech.hpc.filter.JwtAuthenticationFilter;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
//@EnableMethodSecurity
public class SecurityConfig implements WebMvcConfigurer {
    @Resource
    JwtAuthenticationFilter jwtAuthenticationFilter;
    @Resource
    AuthenticationEntryPoint authenticationEntryPoint;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable) //关闭csrf
                .sessionManagement(AbstractHttpConfigurer::disable) //关闭Session(不通过Session获取SecurityContext)
//                .authorizeHttpRequests(
//                        authorize -> authorize
//                                    .requestMatchers("/user/**").anonymous()
//                                    .requestMatchers("/target/**").anonymous()
//                                    .requestMatchers("/alert/**").anonymous()
//                                    .requestMatchers("/doc.html", "/webjars/**", "/img.icons/**", "/swagger-resources", "/v2/api-docs", "/favicon.ico").permitAll() //放行API doc相关URL
//                                    .anyRequest().authenticated()
//                )
                .exceptionHandling(
                        exceptionHandle -> exceptionHandle
                                        .authenticationEntryPoint(authenticationEntryPoint) //认证失败
//        							    .accessDeniedHandler(accessDeniedHandler) //鉴权失败
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) //在UsernamePasswordAuthenticationFilter**之前**添加JWT过滤器(为什么?)
                .build();
    }

    /**
     * 开启SpringBoot跨域
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") //允许所有API
                .allowedOriginPatterns("*") //允许所有域名
                .allowCredentials(true) //允许Cookie
                .allowedMethods("*") //允许任何方法
                .allowedHeaders("*") //允许任何header
                .maxAge(3600); //跨域允许时间1h
    }
}

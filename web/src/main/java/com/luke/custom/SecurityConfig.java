package com.luke.custom;


import com.luke.Filter.JwtAuthFilter;
import com.luke.service.impl.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity// 开启网络安全注解
@RequiredArgsConstructor
@EnableMethodSecurity(prePostEnabled = true) // 必须添加此注解
public class SecurityConfig {

    @Autowired
    private final JwtAuthFilter jwtAuthFilter;
    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // 关闭 CSRF（如果需要允许POST、PUT）
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login","/user/login",
                                "/doc.html",// Swagger UI 入口
                                    "/webjars/**",
                                "/swagger-ui/**",     // Swagger 静态资源
                                "/v3/api-docs/**"     // OpenAPI 文档).permitAll() // 允许访问登录接口
                        ).permitAll().anyRequest().authenticated() // 其他请求需要认证
                )
                .formLogin(login -> login
                        .loginProcessingUrl("/login") // 登录请求的 URL
                        .defaultSuccessUrl("/home", true) // 登录成功后跳转页面
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login")
                        .permitAll()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                ;

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
        builder
                .userDetailsService(userDetailsService) // 设置自定义 UserDetailsService
                .passwordEncoder(passwordEncoder());    // 设置密码编码器
        return builder.build();
    }


}

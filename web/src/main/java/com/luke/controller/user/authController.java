package com.luke.controller.user;

import com.luke.exception.LoginException;
import com.luke.result.Result;
import com.luke.utils.JwtUtil;
import com.luke.vo.user.UserLoginVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Tag(name = "用户登录与管理")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class authController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Operation(summary = "用户登录并返回JWT")
    @PostMapping("/login")
    public Result<?> login(@RequestBody UserLoginVo request) {
        try{
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUserName(),
                            request.getUserPassword()
                    )
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            String jwt = jwtUtil.createToken(userDetails.getUsername(),userDetails.getPassword());
            return Result.ok(jwt);

        }catch(LoginException e){
            return Result.fail("用户名或者密码错误");
        }

    }

}

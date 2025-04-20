package com.luke.controller.user;

import ch.qos.logback.core.util.SystemInfo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.luke.entity.User;
import com.luke.result.Result;
import com.luke.service.UserService;
import com.luke.vo.user.UserDetailsVo;
import com.luke.vo.user.UserInfoVo;
import com.luke.vo.user.UserVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Tag(name = "查询用户信息")
@RestController
@RequestMapping("/user")
public class getController {
    @Autowired
    private UserService userService;
    // 1. 查询当前用户信息
    @PreAuthorize("hasAnyRole('GENERAL','LEADER','ZONEKEEPER')")
    @Operation(summary = "查询自己的信息")
    @GetMapping("/me")
    public Result getCurrentUser() {

        // 从SecurityContext获取当前用户
        // 获取认证对象
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 获取用户名
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
//        if (authentication != null && authentication.isAuthenticated()) {
//            // 获取权限集合
//            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
//
//            // 打印每个权限
//            System.out.println("User Authorities:");
//            for (GrantedAuthority authority : authorities) {
//                System.out.println("- " + authority.getAuthority());
//            }
//        }

        //获取权限列表
        //Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, username);
        User user = userService.getOne(queryWrapper);
        UserInfoVo userInfoVo = new UserInfoVo(user);
        return Result.ok(userInfoVo);

    }
    //2.按照学号查询员工信息
    @PreAuthorize("hasAnyRole('ZONEKEEPER','LEADER')")
    @Operation(summary = "按照学号查询员工信息")
    @GetMapping("/getById/{username}")
    public Result getCurrentUser(@PathVariable String username) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername,username);
        User user = userService.getOne(queryWrapper);
        if(user == null) return Result.fail("该员工不存在");
        UserInfoVo userInfoVo = new UserInfoVo(user);
        return Result.ok(userInfoVo);
    }
    @PreAuthorize("hasAnyRole('ZONEKEEPER','LEADER')")
    @Operation(summary = "按照姓名查询员工信息")
    @GetMapping("/getByName/{username}")
    public Result getCurrentUserByName(@PathVariable String username) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername,username);
        List<User> user = userService.list(queryWrapper);
        if(user == null) return Result.fail("该员工不存在");
        List<UserInfoVo> userInfoVo = new ArrayList<>();
        for(User cur:user){
            userInfoVo.add(new UserInfoVo(cur));
        }

        return Result.ok(userInfoVo);
    }

    // 3. 按照分区，分页查询用户
    @PreAuthorize("hasAnyRole('ZONEKEEPER','LEADER')")
    @Operation(summary = "按照片区分页查询值班员信息")
    @GetMapping("/getByZone/{zone}")
    public Result<IPage<UserInfoVo>> listUsers(
            @PathVariable String zone,
            @RequestParam(defaultValue = "1") int page,@RequestParam(defaultValue = "10") int size) {
        IPage<UserInfoVo> user = userService.selectByZone(page,size,zone);
        return Result.ok(user);
    }
}

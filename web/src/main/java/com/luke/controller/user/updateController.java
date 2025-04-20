package com.luke.controller.user;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.luke.entity.User;
import com.luke.mapper.UserMapper;
import com.luke.result.Result;
import com.luke.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.websocket.server.PathParam;
import jodd.util.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
    和用户相关的操作
 */
@Tag(name = "更新用户信息")
@RestController
@RequestMapping("/user")
public class updateController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserMapper userMapper;

    // 1. 创建用户（批量/单个）
    @PreAuthorize("hasAnyRole('ZONEKEEPER','LEADER','GENERAL')")
    @Operation(summary = "根据学号批量新建用户")
    @PostMapping("/create")
    //@PreAuthorize("hasRole('ADMIN')")
    public Result<List<String>> createUser(@RequestBody List<String> userList) {
        // 密码加密存储
        List<String> failedList = userService.createUsers(userList);
        if(failedList.size() > 0) {
            return Result.fail(failedList);
        }
        return Result.ok();
    }

    @PreAuthorize("hasAnyRole('ZONEKEEPER','LEADER','GENERAL')")// 2. 更新用户信息
    @Operation(summary = "所有员工更新自己的信息，不包括密码")
    @PostMapping("/update/info")
    //@PreAuthorize("#userId == principal.id or hasRole('ADMIN')")
    public Result updateUserInfo(
            @RequestBody User user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //获取用户名
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        if(userService.updateUserInfo(username,user)) {// 管理员可修改负责片区
            return Result.ok();
        }
        else return Result.fail("更新失败");

    }

    @PreAuthorize("hasAnyRole('ZONEKEEPER','LEADER','GENERAL')")
    @Operation(summary = "所有用户更新自己的密码")
    @PostMapping("/update/password")
    //@PreAuthorize("#userId == principal.id or hasRole('ADMIN')")
    public Result updateUserPassword(
            @RequestBody String password) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //获取用户名
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(User::getUsername, username);
        updateWrapper.set(User::getPassword, BCrypt.hashpw(password, BCrypt.gensalt()));
        int rows = userMapper.update(null, updateWrapper);
        return rows > 0 ?Result.ok():Result.fail("更新失败");
    }

    @PreAuthorize("hasAnyRole('LEADER')")
    @Operation(summary = "负责人更新员工的密码")
    @PostMapping("/update/password/{username}")
    public Result updatePassword( @PathVariable String username,
            @RequestBody String password) {

        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(User::getUsername, username);
        updateWrapper.set(User::getPassword, BCrypt.hashpw(password, BCrypt.gensalt()));
        int rows = userMapper.update(null, updateWrapper);
        return rows > 0 ?Result.ok():Result.fail("更新失败");
    }

    @PreAuthorize("hasAnyRole('LEADER','ZONEKEEPER')")
    @Operation(summary = "负责人更新值班员负责的楼栋片区和离职状态信息")
    @PostMapping("/update/user/{username}")
    //@PreAuthorize("#userId == principal.id or hasRole('ADMIN')")
    public Result updateUser(
            @PathVariable String username,
            @RequestBody User user) {
        if(userService.updateUserKeyInfo(username,user)){
            return Result.ok();
        }
        return Result.fail("更新失败，请检查学号是否正确");
    }


}

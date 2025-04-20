package com.luke.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.luke.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.luke.result.Result;
import com.luke.vo.user.UserInfoVo;
import com.luke.vo.user.UserLoginVo;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

/**
* @author 23992
* @description 针对表【user】的数据库操作Service
* @createDate 2025-02-27 16:03:09
*/
public interface UserService extends IService<User> {

    List<String> createUsers(List<String> userList);
    IPage<UserInfoVo> selectByZone(int current, int size, String zone);
    boolean updateUserInfo(String username,User user);
    boolean updateUserKeyInfo(String username, User user);
    User getUserByUsername(String username);
}

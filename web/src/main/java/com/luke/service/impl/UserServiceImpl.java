package com.luke.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.luke.entity.User;
import com.luke.result.Result;
import com.luke.service.UserService;
import com.luke.mapper.UserMapper;
import com.luke.vo.user.UserInfoVo;
import com.luke.vo.user.UserLoginVo;
import jodd.util.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
* @author 23992
* @description 针对表【user】的数据库操作Service实现
* @createDate 2025-02-27 16:03:09
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<String> createUsers(List<String> userList) {
        List<String> failedList = new ArrayList<>();

        for (String username : userList) {

            User user = new User();
            user.setUsername(username);
            // 获取学号后六位作为密码
            String password = username.substring(username.length() - 6);
            // 对密码进行加密
            String encryptedPassword = BCrypt.hashpw(password, BCrypt.gensalt()); // 使用 BCrypt 进行加密
            user.setPassword(encryptedPassword);

            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getUsername, user.getUsername()); // 根据学号查询

            User existingUser = userMapper.selectOne(queryWrapper); // 查询是否存在
            if (existingUser == null) {
                // 如果不存在，执行插入
                userMapper.insert(user);
                user.setIsActive(1); // 设置为在职（1）
            } else {
                // 如果存在，添加到list中
                failedList.add(username);
            }
        }
        return failedList; // 返回失败的用户列表
    }

    @Override
    public IPage<UserInfoVo> selectByZone(int current, int size, String zone) {
        Page<User> page  = new Page<User>(current,size);
        userMapper.selectUserByZone(page,zone);
        return page.convert(user -> new UserInfoVo(user));
    }

    @Override
    public boolean updateUserInfo(String username,User user) {
        // 3. 构建动态更新条件

        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(User::getUsername, username); // WHERE id = ?

        // 4. 遍历User 字段，动态设置非空值
        // 注意：需排除 id、username 等不可修改字段
        if (user.getRealName() != null) {
            updateWrapper.set(User::getRealName, user.getRealName());
        }
        if (user.getGender() != null) {
            updateWrapper.set(User::getGender, user.getGender());
        }
        if (user.getGrade() != null) {
            updateWrapper.set(User::getGrade, user.getGrade());
        }
        if (user.getPhone() != null) {
            updateWrapper.set(User::getPhone, user.getPhone());
        }
        if (user.getDorm() != null) {
            updateWrapper.set(User::getDorm, user.getDorm());
        }
        if (user.getDormNumber() != null) {
            updateWrapper.set(User::getDormNumber, user.getDormNumber());
        }
        if(user.getEmailAddress() != null) {
            updateWrapper.set(User::getEmailAddress, user.getEmailAddress());
        }
        /*if (user.getPosition() != null) {
            updateWrapper.set("position", user.getPosition());
        }
         */
        // 6. 执行更新
        int rows = userMapper.update(null, updateWrapper);
        return rows > 0 ? true: false;
    }

    @Override
    public boolean updateUserKeyInfo(String username, User user) {
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(User::getUsername, username);
        if(user.getManagedZone()!=null) updateWrapper.set(User::getManagedZone, user.getManagedZone());
        if(user.getIsActive()!=null) updateWrapper.set(User::getIsActive,user.getIsActive());
        if(user.getPosition()!=null)updateWrapper.set(User::getPosition,user.getPosition());

        int rows = userMapper.update(null, updateWrapper);
        return rows > 0 ?true:false;
    }

    @Override
    public User getUserByUsername(String username) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, username);
        return userMapper.selectOne(queryWrapper);
    }

}





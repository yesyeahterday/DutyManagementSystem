package com.luke.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.luke.entity.User;
import com.luke.mapper.UserMapper;
import com.luke.vo.user.UserDetailsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

        // 你的用户数据访问层
        @Autowired
        private UserMapper userMapper;

        @Override
        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("username", username); // 根据学号查询

            User user = userMapper.selectOne(queryWrapper);

            if(user == null) {
                throw new UsernameNotFoundException("用户不存在：" + username);
                }
            UserDetailsVo  userDetailsVo = new UserDetailsVo(user);
            return userDetailsVo;
        }
}


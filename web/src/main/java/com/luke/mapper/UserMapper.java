package com.luke.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.luke.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
* @author 23992
* @description 针对表【user】的数据库操作Mapper
* @createDate 2025-02-27 16:03:09
* @Entity com.luke.entity.User
*/
public interface UserMapper extends BaseMapper<User> {
     IPage<User> selectUserByZone(Page<User> page, @Param("zone") String zone);

     Long findUserIdByUsername(String username);
}





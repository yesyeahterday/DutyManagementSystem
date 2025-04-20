package com.luke.service;

import com.luke.entity.Dorm;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 23992
* @description 针对表【dorm】的数据库操作Service
* @createDate 2025-02-27 16:03:09
*/
public interface DormService extends IService<Dorm> {

    boolean checkDormNameExists(String name);

    boolean updatePartial(Dorm dorm);
}

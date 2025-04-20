package com.luke.service;

import com.luke.entity.Zone;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 23992
* @description 针对表【zone(片区信息表)】的数据库操作Service
* @createDate 2025-02-27 16:03:09
*/
public interface ZoneService extends IService<Zone> {

    boolean checkZoneExists(String zone);
}

package com.luke.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.luke.entity.Zone;
import com.luke.service.ZoneService;
import com.luke.mapper.ZoneMapper;
import org.springframework.stereotype.Service;

/**
* @author 23992
* @description 针对表【zone(片区信息表)】的数据库操作Service实现
* @createDate 2025-02-27 16:03:09
*/
@Service
public class ZoneServiceImpl extends ServiceImpl<ZoneMapper, Zone>
    implements ZoneService{

    @Override
    public boolean checkZoneExists(String zoneName) {
        return this.lambdaQuery()
                .eq(Zone::getName, zoneName)
                .exists();
    }
}





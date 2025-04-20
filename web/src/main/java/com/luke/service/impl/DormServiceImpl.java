package com.luke.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.luke.entity.Dorm;
import com.luke.service.DormService;
import com.luke.mapper.DormMapper;
import org.springframework.stereotype.Service;

/**
* @author 23992
* @description 针对表【dorm】的数据库操作Service实现
* @createDate 2025-02-27 16:03:09
*/
@Service
public class DormServiceImpl extends ServiceImpl<DormMapper, Dorm>
    implements DormService{

    @Override
    public boolean checkDormNameExists(String dormName) {
        return this.lambdaQuery()
                .eq(Dorm::getName, dormName)
                .eq(Dorm::getIsDeleted, 0)
                .exists();
    }

    @Override
    public boolean updatePartial(Dorm dorm) {
        return this.lambdaUpdate()
                .eq(Dorm::getId, dorm.getId())
                .set(dorm.getName() != null, Dorm::getName, dorm.getName())
                .set(dorm.getGenderType() != null, Dorm::getGenderType, dorm.getGenderType())
                .set(dorm.getZone() != null, Dorm::getZone, dorm.getZone())
                .update();
    }
}





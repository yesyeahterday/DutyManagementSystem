package com.luke.mapper;

import com.luke.entity.Dorm;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author 23992
* @description 针对表【dorm】的数据库操作Mapper
* @createDate 2025-02-27 16:03:09
* @Entity com.luke.entity.Dorm
*/
public interface DormMapper extends BaseMapper<Dorm> {

    List<Long> selectBuildingIdsByZone(String zone);
}





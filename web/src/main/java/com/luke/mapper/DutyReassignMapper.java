package com.luke.mapper;

import com.luke.entity.DutyReassign;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.luke.vo.ScheduleVo;

import java.time.LocalDate;
import java.util.List;

/**
* @author 23992
* @description 针对表【duty_reassign】的数据库操作Mapper
* @createDate 2025-02-27 16:03:09
* @Entity com.luke.entity.DutyReassign
*/
public interface DutyReassignMapper extends BaseMapper<DutyReassign> {

    List<ScheduleVo> getReassignedScheduleByUserIdAndDate(Long userId, LocalDate startDate, LocalDate endDate);

    List<Long> getScheduleIdByUserId(Long userId);
}





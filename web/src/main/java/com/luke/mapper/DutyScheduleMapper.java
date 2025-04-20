package com.luke.mapper;

import com.luke.entity.DutySchedule;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.luke.vo.ScheduleVo;
import com.luke.vo.reassign.LeaveScheduleVo;
import io.lettuce.core.dynamic.annotation.Param;

import java.time.LocalDate;
import java.util.List;

/**
* @author 23992
* @description 针对表【duty_schedule】的数据库操作Mapper
* @createDate 2025-02-27 16:03:09
* @Entity com.luke.entity.DutySchedule
*/
public interface DutyScheduleMapper extends BaseMapper<DutySchedule> {

    List<LocalDate> selectDistinctDates(String zone, LocalDate startDate, LocalDate endDate);

    void batchInsert(List<DutySchedule> insertList);

    List<ScheduleVo> selectUnAssignedSchedules(@Param("zone") String zone,
                                               @Param("status") Integer status,
                                               @Param("startDate") LocalDate startDate,
                                               @Param("endDate") LocalDate endDate,
                                               @Param("buildingIds") List<Long> buildingIds);

    List<ScheduleVo> selectAllSchedules(String zone, LocalDate parse, LocalDate parse1, List<Long> buildingIds);

    DutySchedule findByUserIdAndDate(Long userId, LocalDate date);

    List<LeaveScheduleVo> findLeavesByDateRange(LocalDate startDate, LocalDate endDate);

    List<ScheduleVo> findByUserIdAndDateRange(Long userId, LocalDate startDate, LocalDate endDate);

    List<ScheduleVo> findLeaveByUserIdAndDateRange(Long userId, LocalDate startDate, LocalDate endDate);
}





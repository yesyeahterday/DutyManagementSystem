package com.luke.service;

import com.luke.entity.DutyReassign;
import com.baomidou.mybatisplus.extension.service.IService;
import com.luke.result.Result;
import com.luke.vo.ScheduleVo;

import java.util.List;

/**
* @author 23992
* @description 针对表【duty_reassign】的数据库操作Service
* @createDate 2025-02-27 16:03:09
*/
public interface DutyReassignService extends IService<DutyReassign> {

    Result cancelLeave(String username, String date);

    Result applyLeave(String username, String date);

    Result getAvailableLeaves(String startDate, String endDate);

    Result shiftSchedules(String username,long scheduleId);

    Result cancelShiftSchedules(String username, Long scheduleId);

    List<ScheduleVo> getReassignScheduleByUsernameAndDate(String username, String startDate, String endDate);

    Long getScheduleIdByDateAndName(String username, String date);
}

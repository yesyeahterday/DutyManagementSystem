package com.luke.service;

import com.luke.dto.DutyNotificationDTO;
import com.luke.entity.DutySchedule;
import com.baomidou.mybatisplus.extension.service.IService;
import com.luke.result.Result;
import com.luke.vo.ScheduleVo;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
* @author 23992
* @description 针对表【duty_schedule】的数据库操作Service
* @createDate 2025-02-27 16:03:09
*/
public interface DutyScheduleService extends IService<DutySchedule> {

    List<LocalDate> generateEmptySchedule(String zone,LocalDate startDate,LocalDate endDate);

    boolean generateEmptySingleSchedule(String zone, LocalDate parse, Integer salaryRatio);

    boolean updateSalaryRatio(String zone, LocalDate date, String salaryRatio);

    String deleteSingleEmptySchedule(String zone, LocalDate parse);

    List<ScheduleVo> getUnAssignedByZone(String zone, Integer status, LocalDate parse, LocalDate parse1);

    List<ScheduleVo> getAllByZone(String zone, LocalDate parse, LocalDate parse1);

    List<ScheduleVo> getScheduleByUsernameAndDate(String username, String startDate, String endDate);

    List<ScheduleVo> getLeaveScheduleByUsernameAndDate(String username, String startDate, String endDate);

    Result arrangeByDateAndDorm(Long id, String date, Long id1);

    public List<DutyNotificationDTO> getDutyNotificationsByDate(Date date);
}

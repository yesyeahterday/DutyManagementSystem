package com.luke.controller.schedule;

import com.luke.entity.DutySchedule;
import com.luke.result.Result;
import com.luke.service.DutyScheduleService;
import com.luke.vo.ScheduleVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "查询当前排班信息")
@RestController
@RequestMapping("/schedule")
public class getScheduleController {
    @Autowired
    DutyScheduleService dutyScheduleService;

    @Operation(summary = "根据片区查询给定日期范围全部排班情况")
    @PostMapping("/getByZone")
    @PreAuthorize("hasAnyRole('ZONEKEEPER')")
    public Result getSchedule(
            @RequestParam("Zone") String zone,
            @RequestParam("StartDate") String startDate,
            @RequestParam("EndDate") String endDate
    ) {
        List<ScheduleVo> schedules = dutyScheduleService.getAllByZone(zone, LocalDate.parse(startDate),LocalDate.parse(endDate));
        return Result.ok(schedules);

    }


    @Operation(summary = "根据片区查询{未排班、请假中、正常值班、替班中}信息")
    @PostMapping("/getByZone/empty")
    @PreAuthorize("hasAnyRole('ZONEKEEPER')")
    public Result getEmptySchedule(
            @RequestParam("Zone") String zone,
            @RequestParam("Status") Integer status,
            @RequestParam("StartDate") String startDate,
            @RequestParam("EndDate") String endDate
    ) {
        List<ScheduleVo> schedules = dutyScheduleService.getUnAssignedByZone(zone,status, LocalDate.parse(startDate),LocalDate.parse(endDate));

        return Result.ok(schedules);

    }
}

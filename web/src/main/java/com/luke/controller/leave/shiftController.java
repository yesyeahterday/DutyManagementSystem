package com.luke.controller.leave;

import com.luke.result.Result;
import com.luke.service.DutyReassignService;
import com.luke.service.DutyScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@Tag(name = "替班请求")
@RequestMapping("/leave")
public class shiftController {
    @Autowired
    DutyReassignService dutyReassignService;

    // 1.查询指定日期范围的可替班列表,包括返回所需要的scheduleId;
    @Operation(summary = "查询指定日期范围内的可替班列表")
    @GetMapping("/LeavingSchedule")
    @PreAuthorize("hasRole('GENERAL')")
    public Result getAvailableLeaves(@RequestParam("startDate") String startDate,
                                     @RequestParam("endDate") String endDate) {
        //todo 检查该起始日期，如果当前时间小于八点，起始日期不小于该日期，如果大于，起始日期应该大于该日期
        LocalDate parsedDate = LocalDate.parse(startDate);
        // 拼接 8:00 时间点
        LocalDateTime targetDateTime = parsedDate.atTime(8, 0);
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(targetDateTime)) {
            return Result.fail("起始查询时间应该为第二天或八点前查询的当天");
        }
        return dutyReassignService.getAvailableLeaves(startDate, endDate);
    }

    //2.接取替班，修改排班状态，并生成替班记录，根据查询列表中的id.
    @Operation(summary = "接取替班，并生成记录")
    @GetMapping("/shiftSchedule")
    @PreAuthorize("hasRole('GENERAL')")
    public Result shiftSchedules(@RequestParam("ScheduleId") long scheduleId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        //1.查询自身所有的排班以及替班情况
        //todo 检查当前日期是否小于该日期的八点前
        return dutyReassignService.shiftSchedules(username,scheduleId);
    }
    //3.取消替班，修改排班状态，并生成替班记录
    @Operation(summary = "取消已经接取的替班")
    @GetMapping("/cancelShiftSchedule")
    @PreAuthorize("hasRole('GENERAL')")
    public Result cancelShiftSchedules(@RequestParam("Date") String date) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();

        //todo 检查当前时间是否小于给定日期的八点。
        LocalDate parsedDate = LocalDate.parse(date);
        // 拼接 8:00 时间点
        LocalDateTime targetDateTime = parsedDate.atTime(8, 0);
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(targetDateTime)) {
            return Result.fail("已超过执行时间（当天8点前），无法操作");
        }

        Long scheduleId = dutyReassignService.getScheduleIdByDateAndName(username,date);
        if(scheduleId == -1) return Result.fail("不存在该替班记录,无法取消");

        return dutyReassignService.cancelShiftSchedules(username,scheduleId);
    }
}

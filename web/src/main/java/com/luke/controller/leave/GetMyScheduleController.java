package com.luke.controller.leave;

import com.luke.result.Result;
import com.luke.service.DutyReassignService;
import com.luke.service.DutyScheduleService;
import com.luke.vo.ScheduleVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@Tag(name = "查询自己值班列表")
@RequestMapping("/leave")
public class GetMyScheduleController {

    @Autowired
    DutyReassignService dutyReassignService;
    @Autowired
    DutyScheduleService dutyScheduleService;

    // 1. 查询指定日期范围内所有自己的值班信息，包括正常值班以及替班，还有请假中
    @Operation(summary = "查询自己的全部值班信息")
    @PostMapping("/getScheduleByDay")
    @PreAuthorize("hasRole('GENERAL')")
    public Result getMyScheduleByDay(@RequestParam("startDate") String startDate,
                             @RequestParam("endDate") String endDate) {
        //1.身份验证
        Authentication anthentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) anthentication.getPrincipal();
        String username = userDetails.getUsername();
        //2.查询排班表中的记录
        List<ScheduleVo> schedules = dutyScheduleService.getScheduleByUsernameAndDate(username,startDate,endDate);

        //3.查询替班表中的记录
        List<ScheduleVo> reassignedSchedules = dutyReassignService.getReassignScheduleByUsernameAndDate(username,startDate,endDate);

        List<ScheduleVo> allSchedules = new ArrayList<>();
        allSchedules.addAll(schedules);
        allSchedules.addAll(reassignedSchedules);

        return  Result.ok(allSchedules);
    }

    // 2. 查询自己替班的信息
    @Operation(summary = "查询指定日期范围内的自身替班信息")
    @PostMapping("/getShiftByDay")
    @PreAuthorize("hasRole('GENERAL')")
    public Result getShiftByDay(@RequestParam("startDate") String startDate,
                              @RequestParam("endDate") String endDate) {
        //1.身份验证
        Authentication anthentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) anthentication.getPrincipal();
        String username = userDetails.getUsername();
        //2.查询替班表中的记录
        List<ScheduleVo> reassignedSchedules = dutyReassignService.getReassignScheduleByUsernameAndDate(username,startDate,endDate);

        return  Result.ok(reassignedSchedules);
    }

    // 3. 查询自己请假中的班次信息
    @Operation(summary = "查询自身请假中信息")
    @PostMapping("/getLeaveByDay")
    @PreAuthorize("hasRole('GENERAL')")
    public Result getLeaveByDay(@RequestParam("startDate") String startDate,
                              @RequestParam("endDate") String endDate) {
        //1.身份验证
        Authentication anthentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) anthentication.getPrincipal();
        String username = userDetails.getUsername();
        //2.查询排班表中请假中的记录
        List<ScheduleVo> schedules = dutyScheduleService.getLeaveScheduleByUsernameAndDate(username,startDate,endDate);
        return Result.ok(schedules);
    }
}

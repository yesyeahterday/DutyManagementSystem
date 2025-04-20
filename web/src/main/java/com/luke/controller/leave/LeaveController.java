package com.luke.controller.leave;

import com.luke.result.Result;
import com.luke.service.DutyReassignService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@Tag(name = "班次请假")
@RequestMapping("/leave")
public class LeaveController {
    @Autowired
    DutyReassignService dutyReassignService;

    // 1. 提交请假申请,操作数据库，将排班表标记为请假状态
    @Operation(summary = "员工对自身排班记录发起请假")
    @PostMapping("/leaveByDay")
    @PreAuthorize("hasRole('GENERAL')")
    public Result applyLeave(@RequestParam("date") String date) {
        //1.身份验证
        Authentication anthentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) anthentication.getPrincipal();
        String username = userDetails.getUsername();
        //ToDo:检查日期，时间在该日期8:00后无法请假
        LocalDate parsedDate = LocalDate.parse(date);
        // 拼接 8:00 时间点
        LocalDateTime targetDateTime = parsedDate.atTime(8, 0);
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(targetDateTime)) {
            return Result.fail("已超过请假时间（当天八点前）");
        }
        return  dutyReassignService.applyLeave(username,date);
    }

    // 2. 取消请假
    @Operation(summary = "取消该日期的请假状态")
    @PostMapping("/cancelLeaveByDay")
    @PreAuthorize("hasRole('GENERAL')")
    public Result cancelLeave(@RequestParam String date) {
        //1.身份验证
        Authentication anthentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) anthentication.getPrincipal();
        String username = userDetails.getUsername();
        //ToDo:检查日期，时间在该日期8:00后无法取消请假
        //ToDo:检查日期，时间在该日期8:00后无法请假
        LocalDate parsedDate = LocalDate.parse(date);
        // 拼接 8:00 时间点
        LocalDateTime targetDateTime = parsedDate.atTime(8, 0);
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(targetDateTime)) {
            return Result.fail("已超过取消请假时间（当天八点前）");
        }
        return dutyReassignService.cancelLeave(username,date);
    }

}

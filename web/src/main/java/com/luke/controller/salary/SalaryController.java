package com.luke.controller.salary;

import com.luke.dto.DutyDetailRowDTO;
import com.luke.dto.SalaryExcelRowDTO;
import com.luke.dto.SalaryReportResultDTO;
import com.luke.entity.DutySchedule;
import com.luke.result.Result;
//import org.springframework.security.access.prepost.PreAuthorize;
import com.luke.service.DormService;
import com.luke.service.SalaryService;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@Tag(name = "工资统计")
@RequestMapping("/salary")
public class SalaryController {
    @Autowired
    SalaryService salaryService;
    @Autowired
    DormService dormService;

    // 1.按照片区生成指定日期区间的工资报表
    @PostMapping("/generateByZone")
    @Operation(summary = "根据片区生成工资统计")
    @PreAuthorize("hasRole('ZONEKEEPER')")
    public Result generateSalaryReportByZone(
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate
    ) {
        //1.身份验证
        Authentication anthentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) anthentication.getPrincipal();
        String username = userDetails.getUsername();
        //2.检查该日期区间是否已是过去日期
        //ToDo:检查日期，时间在该日期8:00后无法请假
        LocalDate parsedDate = LocalDate.parse(endDate);
        // 拼接 8:00 时间点
        LocalDateTime targetDateTime = parsedDate.atTime(16, 0);
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(targetDateTime)) {
            return Result.fail("结束时间应该为统计期间的第二天");
        }
        Result res = salaryService.getScheduleList(startDate,endDate,username);
        if(res.getCode() != 200) return res;

        List<DutySchedule> scheduleList =(List<DutySchedule>) res.getData();
        System.out.println(scheduleList);
        List<SalaryExcelRowDTO> salaryExcelRowList = salaryService.generateSalaryReport(scheduleList);
        List<DutyDetailRowDTO> dutyDetailRowList = salaryService.generateDutyDetailRows(scheduleList);

        SalaryReportResultDTO report = new SalaryReportResultDTO();
        report.setSalaryList(salaryExcelRowList);
        report.setDutyDetails(dutyDetailRowList);

        return Result.ok(report);
    }
    //TODO 暂时不实现
    //2.生成所有片区指定日期区间的工资报表
    @PostMapping("/generateAll")
    @Operation(summary = "生成所有片区的工资统计")
    @PreAuthorize("hasRole('LEADER')")
    public Result generateSalaryReport(
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate
    ) {
        //2.检查该日期区间是否已是过去的日期
        //ToDo:检查日期，时间在该日期8:00后无法请假
        LocalDate parsedDate = LocalDate.parse(endDate);
        // 拼接 16:00 时间点
        LocalDateTime targetDateTime = parsedDate.atTime(16, 0);
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(targetDateTime)) {
            return Result.fail("结束时间应该为值班时间的第二天");
        }
        //3.检索该日期内所有值班情况，分别查询排班表和替班表生成每个楼栋对应日期的值班员
        //4.根据3，生成每个值班员对应的工资统计表，格式为姓名+学号＋工资
        //5.生成excel文件
        return Result.ok();
    }



}

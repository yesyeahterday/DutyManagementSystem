package com.luke.controller.schedule;

import com.luke.result.Result;
import com.luke.service.DutyScheduleService;
import com.luke.service.UserService;
import com.luke.vo.ScheduleVo;
import de.jollyday.Holiday;
import de.jollyday.HolidayManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Tag(name ="生成排班信息")
@RestController
@RequestMapping("/schedule")
public class emptyController {
    @Autowired
    UserService userService;
    @Autowired
    DutyScheduleService dutyScheduleService;

    public emptyController() {
    }

    // 1. 按照起止日期生成排班计划
    @Operation(summary = "生成指定日期区间周末的空白排班记录")
    @PostMapping("/generate-empty")
    @PreAuthorize("hasRole('ZONEKEEPER')")
    public Result generateSchedules(
            @RequestParam("Zone") String zone,
            @RequestParam("startDate") String startDate,
                @RequestParam("endDate")  String endDate) {
        //根据楼栋、时间范围生成空白排班，其中会自动计算中间调休的日期和正常节假日
        //1.身份验证
        Authentication anthentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) anthentication.getPrincipal();
        String username = userDetails.getUsername();

        if(!userService.getUserByUsername(username).getManagedZone().equals(zone)){
            System.out.println(zone);
            System.out.println(userService.getUserByUsername(username).getManagedZone());
            return Result.fail("请为自己负责的片区生成排班!");
        }

        //为对应片区楼栋下添加对应的空白排班表
        List<LocalDate> failedList = dutyScheduleService.generateEmptySchedule(zone,LocalDate.parse(startDate),LocalDate.parse(endDate));

        //如果有添加失败，返回添加失败的日期集合
        if(failedList.size() > 0) {
            return Result.fail(failedList);
        }
        return Result.ok("全部添加成功");
    }

    // 2. 按照单一日期生成排班计划
    @Operation(summary = "生成指定日期的空白排班记录")
    @PostMapping("/generate-empty/singleDate")
    @PreAuthorize("hasRole('ZONEKEEPER')")
    public Result generateSchedulesBySingleDate(
            @RequestParam("Zone") String zone,
            @RequestParam("Date") String date,
            @RequestParam("Salary_ratio") String salaryRatio
            ) {
        // 根据楼栋、时间范围生成空白排班，其中会自动计算中间调休的日期和正常节假日
        //1.身份验证
        Authentication anthentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) anthentication.getPrincipal();
        String username = userDetails.getUsername();
        if(!userService.getUserByUsername(username).getManagedZone().equals(zone)) {
            return Result.fail("请为自己负责的片区增加排班!");
        }

        if(dutyScheduleService.generateEmptySingleSchedule(zone,LocalDate.parse(date), Integer.valueOf(salaryRatio))) {
            return Result.ok();
        }
        return Result.fail("该日期排班已经存在");

    }


    // 3. 修改指定日期的工资倍数
    @Operation(summary = "修改指定日期的工作倍数")
    @PostMapping("/salary-ratio")
    @PreAuthorize("hasRole('ZONEKEEPER')")
    public Result ratioSchedulesBySingleDate(
            @RequestParam("Zone") String zone,
            @RequestParam("Date") String date,
            @RequestParam("Salary_Ratio") String salaryRatio
            ) {
        //1.身份验证
        Authentication anthentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) anthentication.getPrincipal();
        String username = userDetails.getUsername();
        if(!userService.getUserByUsername(username).getManagedZone().equals(zone)) {
            return Result.fail("请为自己负责的片区调整工资倍数!");
        }
        if(dutyScheduleService.updateSalaryRatio(zone,LocalDate.parse(date),salaryRatio)){
            return Result.ok();
        }

        return Result.fail("没有该排班记录或该工资倍数已更新");
    }

    //4.删除指定的空白排班表日期，如果不是空白，则无法删除。
    @Operation(summary = "删除指定日期的空白排班表")
    @PostMapping("/delete_empty")
    @PreAuthorize("hasRole('ZONEKEEPER')")
    public Result deleteSchedulesBySingleDate(
            @RequestParam("Zone") String zone,
            @RequestParam("Date") String date
        ) {
        //1.身份验证
        Authentication anthentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) anthentication.getPrincipal();
        String username = userDetails.getUsername();
        if(!userService.getUserByUsername(username).getManagedZone().equals(zone)) {
            return Result.fail("请为自己负责的片区修改信息!");
        }
        String state = dutyScheduleService.deleteSingleEmptySchedule(zone,LocalDate.parse(date));
        if(state.equals("success")){
            return Result.ok();
        }
        return Result.fail(state);
    }

}

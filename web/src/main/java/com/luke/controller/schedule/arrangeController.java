package com.luke.controller.schedule;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.luke.dto.ScheduleItemDTO;
import com.luke.dto.ScheduleItemFailedDTO;
import com.luke.entity.Dorm;
import com.luke.entity.User;
import com.luke.mapper.DormMapper;
import com.luke.mapper.UserMapper;
import com.luke.result.Result;
import com.luke.service.DormService;
import com.luke.service.DutyScheduleService;
import com.luke.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//这些排班都没考虑到自己排班冲突的情况，也就是说如果输入的数据不对还是会正常排班
@Tag(name = "给楼栋依次执行排班")
@RestController
@RequestMapping("/schedule")
public class arrangeController {

    @Autowired
    DutyScheduleService dutyScheduleService;
    @Autowired
    UserMapper userMapper;
    @Autowired
    DormService dormService;
    @Autowired
    UserService userService;

    @Operation(summary = "根据真实名字给指定日期和楼栋排班")
    @PostMapping("/scheduleByDateAndDorm")
    @PreAuthorize("hasRole('ZONEKEEPER')")
    public Result scheduleByDateAndDorm(
            @RequestParam("Dorm") String dorm,
            @RequestParam("Date") String date,
            @RequestParam("name") String realName

    ){
        //1.查询该楼栋是否属于自己负责的片区
        Authentication anthentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) anthentication.getPrincipal();
        String username = userDetails.getUsername();

        User manager = userService.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username)
                .eq(User::getIsActive, 1));

        // 获取 dorm 信息
        Dorm dormInfo = dormService.getOne(new LambdaQueryWrapper<Dorm>()
                .eq(Dorm::getName,dorm));
        if (dormInfo == null) {
            return Result.fail("楼栋不存在");
        }
        // 检查该楼栋是否属于当前用户负责的片区
        if (!dormInfo.getZone().equals(manager.getManagedZone())) {
            return Result.fail("您无权限排班该楼栋");
        }
        // 2. 查询该员工是否存在且在职，且属于该片区
        List<User> staffs = userService.list(new LambdaQueryWrapper<User>()
                .eq(User::getRealName, realName)
                .eq(User::getIsActive, 1));

        if (staffs.size() == 0 ) {
            return Result.fail("该员工不存在于该片区");
        }
        for(User user : staffs) {
            if(user.getManagedZone().equals(manager.getManagedZone())) {
                String buildingGender = (String) dormInfo.getGenderType();
                if(!buildingGender.equals(user.getGender())) {
                    return Result.fail("排班失败，该员工的性别和楼栋需求不符");
                }
                return dutyScheduleService.arrangeByDateAndDorm(dormInfo.getId(),date,user.getId());
            }
        }
        return Result.fail("该用户不属于你管理的片区");

    }

    @Operation(summary = "批量排班，每条记录包含楼栋、日期和姓名")
    @PostMapping("/scheduleBatch")
    @PreAuthorize("hasRole('ZONEKEEPER')")
    public Result scheduleBatch(@RequestBody List<ScheduleItemDTO> scheduleList) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();

        User manager = userService.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username)
                .eq(User::getIsActive, 1));
        List<ScheduleItemFailedDTO> failedList = new ArrayList<>();
        for (ScheduleItemDTO item : scheduleList) {
            Dorm dormInfo = dormService.getOne(new LambdaQueryWrapper<Dorm>()
                    .eq(Dorm::getName, item.getDorm()));
            if (dormInfo == null) {
                failedList.add(ScheduleItemFailedDTO.builder()
                        .dorm(item.getDorm())
                        .date(item.getDate())
                        .realName(item.getRealName())
                        .failedReason("楼栋不存在")
                        .build());
                continue;
            }
            if (!dormInfo.getZone().equals(manager.getManagedZone())) {
                failedList.add(ScheduleItemFailedDTO.builder()
                        .dorm(item.getDorm())
                        .date(item.getDate())
                        .realName(item.getRealName())
                        .failedReason("该楼栋不属于你管理的片区")
                        .build());
                continue;
            }

            List<User> staffs = userService.list(new LambdaQueryWrapper<User>()
                    .eq(User::getRealName, item.getRealName())
                    .eq(User::getIsActive, 1));

            boolean scheduled = false;
            String msg = null;
            for (User user : staffs) {
                if (user.getManagedZone().equals(manager.getManagedZone())) {
                    String buildingGender = (String) dormInfo.getGenderType();
                    if(!buildingGender.equals(user.getGender())) {
                        msg = "该员工性别和楼栋要求的不符合,请检查信息";
                        break;
                    }
                    Result tmp = dutyScheduleService.arrangeByDateAndDorm(dormInfo.getId(), item.getDate(), user.getId());
                    if(tmp.getCode() == 200) scheduled = true;
                    else msg = tmp.getData().toString();
                    break;
                }
            }
            if (!scheduled) {
                failedList.add(ScheduleItemFailedDTO.builder()
                        .dorm(item.getDorm())
                        .date(item.getDate())
                        .realName(item.getRealName())
                        .failedReason(msg != null ? msg:"该员工不存在或者不属于你的片区")
                        .build());
            }
        }
        if(failedList.size() > 0) {
            return Result.fail(failedList);
        }

        return Result.ok("批量排班全部完成");
    }

}

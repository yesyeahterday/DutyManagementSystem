package com.luke.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.luke.config.SalaryConfig;
import com.luke.dto.DutyDetailRowDTO;
import com.luke.dto.SalaryExcelRowDTO;
import com.luke.entity.Dorm;
import com.luke.entity.DutyReassign;
import com.luke.entity.DutySchedule;
import com.luke.entity.User;
import com.luke.mapper.DormMapper;
import com.luke.mapper.UserMapper;
import com.luke.result.Result;
import com.luke.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SalaryServiceImpl implements SalaryService {
    @Autowired
    private UserService userService;
    @Autowired
    private DormService dormService;
    @Autowired
    private DutyScheduleService dutyScheduleService;
    @Autowired
    private DutyReassignService dutyReassignService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DormMapper dormMapper;
    @Autowired
    private SalaryConfig salaryConfig;

    @Override
    public List<SalaryExcelRowDTO> generateSalaryReport(List<DutySchedule> scheduleList) {

        Map<Long, BigDecimal> userSalaryMap = new HashMap<>();

        for (DutySchedule schedule : scheduleList) {
            Long finalUserId = schedule.getUserId();

            if (schedule.getStatus() == 2) { // 替班
                System.out.println(finalUserId);
                DutyReassign reassign = dutyReassignService.getOne(new LambdaQueryWrapper<DutyReassign>()
                        .eq(DutyReassign::getOriginalScheduleId, schedule.getId()));
                if (reassign != null) {
                    System.out.println(reassign);
                    finalUserId = reassign.getNewUserId();
                    System.out.println(finalUserId);
                }
            }

            if (finalUserId != null) {
                BigDecimal ratio = schedule.getSalaryRatio() == null ? BigDecimal.ONE : schedule.getSalaryRatio();
                userSalaryMap.put(finalUserId, userSalaryMap.getOrDefault(finalUserId, BigDecimal.ZERO).add(ratio));
            }
        }

        List<SalaryExcelRowDTO> excelRows = new ArrayList<>();
        for (Map.Entry<Long, BigDecimal> entry : userSalaryMap.entrySet()) {
            User user = userMapper.selectById(entry.getKey());
            if (user != null) {
                SalaryExcelRowDTO row = new SalaryExcelRowDTO();
                row.setRealName(user.getRealName());
                row.setUsername(user.getUsername());
                row.setTotalSalary(entry.getValue().multiply(BigDecimal.valueOf(salaryConfig.getSalary()))); // 每天工资假设为10元
                excelRows.add(row);
            }
        }
        return excelRows;
    }

    @Override
    public Result getScheduleList(String startDate, String endDate, String username) {
        User manager = userService.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username)
                .eq(User::getIsActive, 1));
        if (manager == null) {
            return Result.fail("用户不存在或无效");
        }

        String zone = manager.getManagedZone();

        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);


        List<Dorm> dormList = dormService.list(new LambdaQueryWrapper<Dorm>().eq(Dorm::getZone, zone));
        if (dormList.isEmpty()) {
            return Result.fail("该片区无楼栋信息");
        }

        List<Long> dormIds = dormList.stream().map(Dorm::getId).toList();

        List<DutySchedule> scheduleList = dutyScheduleService.list(new LambdaQueryWrapper<DutySchedule>()
                .between(DutySchedule::getDutyDate, start, end.minusDays(1))
                .eq(DutySchedule::getZone, zone));
        return Result.ok(scheduleList);
    }

    @Override
    public List<DutyDetailRowDTO> generateDutyDetailRows(List<DutySchedule> scheduleList) {
        List<DutyDetailRowDTO> detailRows = new ArrayList<>();

        for (DutySchedule schedule : scheduleList) {
            Long finalUserId = schedule.getUserId();

            // 替班处理
            if (schedule.getStatus() == 2) {
                DutyReassign reassign = dutyReassignService.getOne(new LambdaQueryWrapper<DutyReassign>()
                        .eq(DutyReassign::getOriginalScheduleId, schedule.getId()));
                if (reassign != null) {
                    finalUserId = reassign.getNewUserId();
                }
            }

            // 查询楼栋名
            Dorm dorm = dormMapper.selectById(schedule.getBuildingId());
            String dormName = (dorm != null) ? dorm.getName() : "未知楼栋";

            // 查询用户信息
            if (finalUserId != null) {
                User user = userMapper.selectById(finalUserId);
                if (user != null) {
                    DutyDetailRowDTO row = new DutyDetailRowDTO();
                    row.setDormName(dormName);
                    row.setDutyDate(DateTimeFormatter.ofPattern("yyyy-MM-dd").format(
                            schedule.getDutyDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()));
                    if(schedule.getStatus() == 1) row.setRealName(user.getRealName() + "(代转)");
                    else if(schedule.getStatus() == 2) row.setRealName(user.getRealName() + "(替班)");
                    else if(schedule.getStatus() == 0)row.setRealName(user.getRealName());
                    detailRows.add(row);
                }
            }
        }

        return detailRows;
    }


}

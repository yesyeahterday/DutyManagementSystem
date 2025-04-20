package com.luke.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.luke.dto.DutyNotificationDTO;
import com.luke.entity.Dorm;
import com.luke.entity.DutyReassign;
import com.luke.entity.DutySchedule;
import com.luke.entity.User;
import com.luke.mapper.DormMapper;
import com.luke.mapper.DutyReassignMapper;
import com.luke.mapper.UserMapper;
import com.luke.result.Result;
import com.luke.service.DutyScheduleService;
import com.luke.mapper.DutyScheduleMapper;
import com.luke.vo.ScheduleVo;
import net.bytebuddy.asm.Advice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
* @author 23992
* @description 针对表【duty_schedule】的数据库操作Service实现
* @createDate 2025-02-27 16:03:09
*/
@Service
public class DutyScheduleServiceImpl extends ServiceImpl<DutyScheduleMapper, DutySchedule>
    implements DutyScheduleService{
    @Autowired
    private DutyScheduleMapper dutyScheduleMapper;
    @Autowired
    private DormMapper dormMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DutyReassignMapper dutyReassignMapper;

    private final UserServiceImpl userServiceImpl;

    public DutyScheduleServiceImpl(UserServiceImpl userServiceImpl) {
        this.userServiceImpl = userServiceImpl;
    }

    @Override
    public List<LocalDate> generateEmptySchedule(String zone,LocalDate startDate,LocalDate endDate) {

        // 1.遍历日期，找出节假日和周末
        List<LocalDate> dateList = new ArrayList<>();
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            boolean isWeekend = (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY);
            LocalDate finalDate = date;
            //boolean isHoliday = holidays.stream().anyMatch(h -> h.getDate().equals(finalDate));
            if (isWeekend ) {
                dateList.add(date);
            }
        }
        //2.查找已经存在的日期，去除已经存在的日期
        List<LocalDate> existingDates = dutyScheduleMapper.selectDistinctDates(zone,startDate,endDate);
        dateList.removeAll(existingDates);
        if(dateList.isEmpty()){
            return existingDates;
        }
        else{
            //3.循环添加对应的空白值班记录
            List<Long> buildingIds = dormMapper.selectBuildingIdsByZone(zone);
            List<DutySchedule> insertList = new ArrayList<>();
            for (Long buildingId : buildingIds) {
                for (LocalDate date : dateList) {
                    DutySchedule schedule = new DutySchedule();
                    schedule.setBuildingId(buildingId);
                    schedule.setZone(zone);
                    schedule.setDutyDate(Date.valueOf(date));  // LocalDate 转换为 java.sql.Date 存入数据库
                    schedule.setSalaryRatio(new BigDecimal("1"));  // 默认工资倍数为 1.0
                    schedule.setUserId(null);  // 还未分配值班员
                    schedule.setStatus(3);  // 3 表示 "待分配"
                    schedule.setVersion(1);
                    schedule.setHistoryFlag(0);
                    schedule.setIsDeleted(0);
                    insertList.add(schedule);
                }
            }
            if (!insertList.isEmpty()) {
                dutyScheduleMapper.batchInsert(insertList);
            }

        }

        return existingDates;
    }

    @Override
    public boolean generateEmptySingleSchedule(String zone, LocalDate date, Integer salaryRatio) {

        List<LocalDate> existingDates = dutyScheduleMapper.selectDistinctDates(zone,date,date);
        if(!existingDates.isEmpty()){
            return false;
        }
        else{
            //3.循环添加对应的空白值班记录
            List<Long> buildingIds = dormMapper.selectBuildingIdsByZone(zone);
            List<DutySchedule> insertList = new ArrayList<>();
            for (Long buildingId : buildingIds) {
                    DutySchedule schedule = new DutySchedule();
                    schedule.setBuildingId(buildingId);
                    schedule.setZone(zone);
                    schedule.setDutyDate(Date.valueOf(date));  // LocalDate 转换为 java.sql.Date 存入数据库
                    schedule.setSalaryRatio(BigDecimal.valueOf(salaryRatio));  // 默认工资倍数为 1.0
                    schedule.setUserId(null);  // 还未分配值班员
                    schedule.setStatus(3);  // 3 表示 "待分配"
                    schedule.setVersion(1);
                    schedule.setHistoryFlag(0);
                    schedule.setIsDeleted(0);
                    insertList.add(schedule);
            }
            if (!insertList.isEmpty()) {
                dutyScheduleMapper.batchInsert(insertList);
            }
        }
        return true;
    }

    @Override
    public boolean updateSalaryRatio(String zone, LocalDate date, String salaryRatio) {
        List<LocalDate> existingDates = dutyScheduleMapper.selectDistinctDates(zone,date,date);
        if(existingDates.isEmpty()) {
            return false;
        }
        List<Long> buildingIds = dormMapper.selectBuildingIdsByZone(zone);
        //更新所有date，id的salary_ratio;
        // 构建 UpdateWrapper
        LambdaUpdateWrapper<DutySchedule> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(DutySchedule::getDutyDate, date) // 设置日期条件
                .in(DutySchedule::getBuildingId, buildingIds) // 设置楼栋 ID 条件
                .set(DutySchedule::getSalaryRatio, new BigDecimal(salaryRatio)); // 更新 salary_ratio 字段

        // 执行更新操作
        int rowsAffected = dutyScheduleMapper.update(null, updateWrapper);

        return rowsAffected > 0;
    }

    @Override
    public String deleteSingleEmptySchedule(String zone, LocalDate parse) {
        List<LocalDate> existingDates = dutyScheduleMapper.selectDistinctDates(zone,parse,parse);
        if(existingDates.isEmpty()) {
            return "不存在该空白排班日期";
        }
        List<Long> buildingIds = dormMapper.selectBuildingIdsByZone(zone);

        // 检查是否有该日期和楼栋的排班记录且 status != 3
        LambdaQueryWrapper<DutySchedule> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DutySchedule::getDutyDate, parse)
                .in(DutySchedule::getBuildingId, buildingIds)
                .ne(DutySchedule::getStatus, 3);  // 检查是否有不等于 status=3 的记录

        List<DutySchedule> schedules = dutyScheduleMapper.selectList(queryWrapper);

        if (!schedules.isEmpty()) {
            return "存在非空白排班记录，无法删除";  // 如果存在符合条件的排班记录，返回提示
        }

        // 如果不存在非空白排班记录，逐条删除空白排班记录
        LambdaQueryWrapper<DutySchedule> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(DutySchedule::getDutyDate, parse)
                .in(DutySchedule::getBuildingId, buildingIds);

        int deleteCount = dutyScheduleMapper.delete(deleteWrapper);

        if (deleteCount > 0) {
            return "success";
        } else {
            return "没有空白排班记录需要删除";
        }
    }

    @Override
    public List<ScheduleVo> getUnAssignedByZone(String zone, Integer status, LocalDate startDate, LocalDate endDate) {
        List<LocalDate> existingDates = dutyScheduleMapper.selectDistinctDates(zone,startDate,endDate);
        if(existingDates.isEmpty()) {
            return null;
        }
        List<Long> buildingIds = dormMapper.selectBuildingIdsByZone(zone);
        List<ScheduleVo> scheduleVoList = dutyScheduleMapper.selectUnAssignedSchedules(zone,status,startDate,endDate,buildingIds);

        return scheduleVoList;
    }

    @Override
    public List<ScheduleVo> getAllByZone(String zone, LocalDate parse, LocalDate parse1) {
        List<LocalDate> existingDates = dutyScheduleMapper.selectDistinctDates(zone,parse,parse1);
        if(existingDates.isEmpty()) {
            return null;
        }
        List<Long> buildingIds = dormMapper.selectBuildingIdsByZone(zone);
        List<ScheduleVo> scheduleVoList = dutyScheduleMapper.selectAllSchedules(zone,parse,parse1,buildingIds);

        return scheduleVoList;
    }

    @Override
    public List<ScheduleVo> getScheduleByUsernameAndDate(String username, String startDate, String endDate) {
        Long userId = userMapper.findUserIdByUsername(username);
        List<ScheduleVo> schedules = dutyScheduleMapper.findByUserIdAndDateRange(userId,LocalDate.parse(startDate),LocalDate.parse(endDate));
        return schedules;
    }

    @Override
    public List<ScheduleVo> getLeaveScheduleByUsernameAndDate(String username, String startDate, String endDate) {
        Long userId = userMapper.findUserIdByUsername(username);
        List<ScheduleVo> schedules = dutyScheduleMapper.findLeaveByUserIdAndDateRange(userId,LocalDate.parse(startDate),LocalDate.parse(endDate));
        return schedules;
    }

    @Override
    public Result arrangeByDateAndDorm(Long id, String date, Long id1) {
        // 1. 获取该楼栋该日期的排班记录
        DutySchedule schedule = dutyScheduleMapper.selectOne(new LambdaQueryWrapper<DutySchedule>()
                .eq(DutySchedule::getBuildingId, id)
                .eq(DutySchedule::getDutyDate, LocalDate.parse(date)));

        if (schedule == null) {
            return Result.fail("未找到该楼栋该日期的排班记录");
        }

        // 检查排班状态是否为“请假中”或“待分配”
        if (schedule.getStatus() != 1 && schedule.getStatus() != 3) {
            return Result.fail("该日期排班已确认，无法变更");
        }

        // 4. 更新排班记录
        schedule.setUserId(id1);
        schedule.setStatus(0); // 正常
        dutyScheduleMapper.updateById(schedule);
        return Result.ok("排班成功");
    }

    @Override
    public List<DutyNotificationDTO> getDutyNotificationsByDate(java.util.Date date) {
        LambdaQueryWrapper<DutySchedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DutySchedule::getDutyDate, date);
        wrapper.in(DutySchedule::getStatus, 0, 2); // 仅查询正常和已替班的

        List<DutySchedule> schedules = this.list(wrapper);
        List<DutyNotificationDTO> result = new ArrayList<>();

        for (DutySchedule schedule : schedules) {
            Long userId = schedule.getUserId();
            Long buildingId = schedule.getBuildingId();
            String status;

            if (schedule.getStatus() == 2) { // 替班
                DutyReassign reassign = dutyReassignMapper.selectOne(
                        new LambdaQueryWrapper<DutyReassign>()
                                .eq(DutyReassign::getOriginalScheduleId, schedule.getId())
                );
                if (reassign != null) {
                    userId = reassign.getNewUserId();
                }
                status = "替班";
            } else {
                status = "值班";
            }

            User user = userMapper.selectById(userId);
            if (user == null) continue;
            Dorm dorm = dormMapper.selectById(buildingId);
            if (dorm == null) continue;
            if(user.getEmailAddress() == null)  continue;

            DutyNotificationDTO dto = new DutyNotificationDTO();
            dto.setDorm(dorm.getName() + "栋 ");
            dto.setRealName(user.getRealName());
            dto.setEmail(user.getEmailAddress());
            dto.setDate(schedule.getDutyDate());
            dto.setStatus(status);
            result.add(dto);
        }

        return result;
    }
}





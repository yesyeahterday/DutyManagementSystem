package com.luke.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.luke.entity.DutyReassign;
import com.luke.entity.DutySchedule;
import com.luke.mapper.DormMapper;
import com.luke.mapper.DutyScheduleMapper;
import com.luke.mapper.UserMapper;
import com.luke.result.Result;
import com.luke.service.DutyReassignService;
import com.luke.mapper.DutyReassignMapper;
import com.luke.vo.ScheduleVo;
import com.luke.vo.reassign.LeaveScheduleVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
* @author 23992
* @description 针对表【duty_reassign】的数据库操作Service实现
* @createDate 2025-02-27 16:03:09
*/
@Service
public class DutyReassignServiceImpl extends ServiceImpl<DutyReassignMapper, DutyReassign>
    implements DutyReassignService{

    @Autowired
    private DutyScheduleMapper dutyScheduleMapper;
    @Autowired
    private DutyReassignMapper dutyReassignMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DormMapper dormMapper;

    public Result applyLeave(String username, String date) {
        Long userId = userMapper.findUserIdByUsername(username);
        DutySchedule dutySchedule = dutyScheduleMapper.findByUserIdAndDate(userId, LocalDate.parse(date));

        if (dutySchedule == null) {
            return Result.fail("该日期没有排班记录");
        }
        if (dutySchedule.getStatus() != 0) {
            return Result.fail("该班次无法请假");
        }

        dutySchedule.setStatus(1);
        dutyScheduleMapper.updateById(dutySchedule);
        return Result.ok();
    }

    @Override
    public Result getAvailableLeaves(String startDate, String endDate) {
        List<LeaveScheduleVo> leaves = dutyScheduleMapper.findLeavesByDateRange(LocalDate.parse(startDate), LocalDate.parse(endDate));
        return Result.ok(leaves);

    }

    @Override
    public Result shiftSchedules(String username,long scheduleId) {
        Long userId = userMapper.findUserIdByUsername(username);
        String userGender = userMapper.selectById(userId).getGender();
        //1.查看该班次状态
        DutySchedule dutySchedule = dutyScheduleMapper.selectById(scheduleId);

        int status = dutySchedule.getStatus();
        if(status != 1) return Result.fail("该班次不处于请假状态");
        if(dutySchedule.getUserId() == userId) return Result.fail("该班次是自己的排班班次，请执行取消请假");

        Long buildingId = dutyScheduleMapper.selectById(dutySchedule.getBuildingId()).getBuildingId();
        String buildingGender =(String) dormMapper.selectById(buildingId).getGenderType();
        if(!buildingGender.equals(userGender)) {
            return Result.fail("只能替班同性别学生的楼栋，请检查该楼栋属性");
        }
        LambdaQueryWrapper<DutySchedule> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DutySchedule::getUserId,userId).eq(DutySchedule::getDutyDate,dutySchedule.getDutyDate()).eq(DutySchedule::getStatus,0);

        System.out.println(dutyScheduleMapper.selectList(queryWrapper));
        if(dutyScheduleMapper.selectList(queryWrapper).size() != 0){
            return Result.fail("当前日期已有排班，无法替班");
        }

        LambdaQueryWrapper<DutyReassign> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(DutyReassign::getNewUserId,userId);
        List<DutyReassign> dutyReassigns = dutyReassignMapper.selectList(queryWrapper1);

        for(DutyReassign dutyReassign:dutyReassigns){
            DutySchedule curDuty = dutyScheduleMapper.selectById(dutyReassign.getOriginalScheduleId());
            if(curDuty != null && curDuty.getDutyDate() == dutySchedule.getDutyDate()) return Result.fail("该日已经在其他楼栋有替班");

        }
        //修改排班记录为替班状态
        dutySchedule.setStatus(2);
        boolean updated = (dutyScheduleMapper.updateById(dutySchedule) == 1);
        if(!updated){
            return Result.fail("该替班已经被接取，请刷新页面");
        }
        // 生成替班记录
        DutyReassign dutyReassign = new DutyReassign();
        dutyReassign.setOriginalScheduleId(scheduleId);
        dutyReassign.setNewUserId(userId);
        dutyReassign.setReassignTime(new Date());
        dutyReassignMapper.insert(dutyReassign);

        return Result.ok("替班成功");
    }

    @Override
    public Result cancelShiftSchedules(String username, Long scheduleId) {
        Long userId = userMapper.findUserIdByUsername(username);
        //1.查看该班次状态度
        DutySchedule dutySchedule = dutyScheduleMapper.selectById(scheduleId);
        int status = dutySchedule.getStatus();
        if(status != 2) return Result.fail("该班次处于不处于替班状态");

        LambdaQueryWrapper<DutyReassign> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(DutyReassign::getNewUserId,userId).eq(DutyReassign::getOriginalScheduleId,scheduleId);
        List<DutyReassign> dutyReassigns = dutyReassignMapper.selectList(queryWrapper1);
        if(dutyReassigns == null) return Result.fail("不是该班次的替班员");

        //删除替班记录
        dutyReassignMapper.deleteById(dutyReassigns.get(0).getId());
        //更改排班表为请假状态
        dutySchedule.setStatus(1);
        dutyScheduleMapper.updateById(dutySchedule);
        return Result.ok("成功取消该班次替班");
    }

    @Override
    public List<ScheduleVo> getReassignScheduleByUsernameAndDate(String username, String startDate, String endDate) {
        Long userId = userMapper.findUserIdByUsername(username);
        List<ScheduleVo> reassignedSchedules = dutyReassignMapper.getReassignedScheduleByUserIdAndDate(userId,LocalDate.parse(startDate),LocalDate.parse(endDate));
        return reassignedSchedules;
    }

    @Override
    public Long getScheduleIdByDateAndName(String username, String date) {
        Long userId = userMapper.findUserIdByUsername(username);

        List<Long> scheduleIds = dutyReassignMapper.getScheduleIdByUserId(userId);
        if(scheduleIds == null) return (long) -1;

        for(Long scheduleId:scheduleIds){
            DutySchedule dutySchedule = dutyScheduleMapper.selectById(scheduleId);
            System.out.println(dutySchedule);
            Date scheduleDate = dutySchedule.getDutyDate();
            LocalDate schedule = scheduleDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            if(schedule.equals(LocalDate.parse(date))) {
                return dutySchedule.getId();
            }
        }

        return (long) -1;
    }


    public Result cancelLeave(String username, String date) {
        Long userId = userMapper.findUserIdByUsername(username);
        DutySchedule dutySchedule = dutyScheduleMapper.findByUserIdAndDate(userId, LocalDate.parse(date));

        if (dutySchedule == null || dutySchedule.getStatus() != 1) {
            return Result.fail("该班次不处于请假状态，无法取消");
        }

        //判断该日期是否接取了替班
        LambdaQueryWrapper<DutyReassign> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(DutyReassign::getNewUserId,userId);
        List<Long> scheduleIds = dutyReassignMapper.getScheduleIdByUserId(userId);

        for(Long scheduleId:scheduleIds){
            DutySchedule tmpDutySchedule = dutyScheduleMapper.selectById(scheduleId);
            Date scheduleDate = tmpDutySchedule.getDutyDate();
            LocalDate schedule = scheduleDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            if(schedule.equals(LocalDate.parse(date))) {
                return Result.fail("请先取消其他楼栋的替班");
            }
        }

        dutySchedule.setStatus(0);
        boolean updated = (dutyScheduleMapper.updateById(dutySchedule) == 1);
        if(!updated) {
            return Result.fail("该班次已被替班，请刷新页面");
        }
        return Result.ok();
    }
}





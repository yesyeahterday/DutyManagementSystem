package com.luke.vo.reassign;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class LeaveScheduleVo {
    int scheduleId;
    String name;  //楼栋名称
    LocalDate date;
    BigDecimal salary_ratio;
    String gender_type;
}

package com.luke.vo.schedule;

import lombok.Data;

import java.util.Date;

@Data
public class GenerateScheduleVo {
    String zone;
    Date startDate;
    Date endDate;
}

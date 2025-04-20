package com.luke.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ScheduleItemFailedDTO {
    private String date;
    private String dorm;
    private String realName;
    private String failedReason;

}

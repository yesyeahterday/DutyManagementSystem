package com.luke.dto;

import lombok.Data;

import java.util.Date;

//用于生成通知值班员值班所需的信息
@Data
public class DutyNotificationDTO {
    private String Dorm;
    private String realName;
    private String Email;
    private Date date;
    private String status;
}

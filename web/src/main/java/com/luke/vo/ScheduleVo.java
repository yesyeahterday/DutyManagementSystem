package com.luke.vo;
import lombok.Data;
import java.time.LocalDate;

@Data
public class ScheduleVo {
    String name;  //楼栋名称
    LocalDate date;
    String status;
    String username;
    String realname;
    String gender_type;
}

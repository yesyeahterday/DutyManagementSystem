package com.luke.vo.user;

import com.luke.entity.User;
import com.luke.entity.Zone;
import lombok.Data;

@Data
public class UserInfoVo {
    String userName;
    String realName;
    Integer isActive;
    String zone;
    String dorm;
    String phone;
    String gender;
    String grade;
    String dormNumber;
    String position;


    public  UserInfoVo(User user){
        userName = user.getUsername();
        realName = user.getRealName();
        isActive = user.getIsActive();
        zone = user.getManagedZone();
        dorm = user.getDormNumber();
        phone = user.getPhone();
        gender = user.getGender();
        grade = user.getGrade();
        dormNumber = user.getDormNumber();
        position = user.getPosition();
    }

}

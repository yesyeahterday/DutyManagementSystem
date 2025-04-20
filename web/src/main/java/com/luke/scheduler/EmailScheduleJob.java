package com.luke.scheduler;

import com.luke.dto.DutyNotificationDTO;
import com.luke.entity.DutySchedule;
import com.luke.service.DutyScheduleService;
import com.luke.service.MailService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class EmailScheduleJob extends QuartzJobBean {

    @Autowired
    private MailService mailService;
    @Autowired
    private DutyScheduleService dutyScheduleService;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        //1.TO DO 获取需要值班的列表
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        Date date = java.sql.Date.valueOf(tomorrow);
        List<DutyNotificationDTO> notificationList = dutyScheduleService.getDutyNotificationsByDate(date);
        //2.to do 根据list循环发送对应的调用mailsevice发送
        for(DutyNotificationDTO notification:notificationList){

            String to = notification.getEmail();
            String subject = "生活园区勤助部值班提醒";
            StringBuilder content = new StringBuilder();
            content.append("同学您好，以下是您明天的值班提醒：\n\n");
            content.append("姓名：").append(notification.getRealName()).append("\n")
                    .append("楼栋：").append(notification.getDorm()).append("\n")
                    .append("日期：").append(new SimpleDateFormat("yyyy-MM-dd").format(notification.getDate())).append("\n")
                    .append("状态：").append(notification.getStatus()).append("\n")
                    .append("邮箱：").append(notification.getEmail()).append("\n")
                    .append("---------------------------\n");
            content.append("请准时到岗，认真履行值班职责。如有疑问，请联系管理员。");
            mailService.sendSimpleMail(to, subject, content.toString());


        }

        //3.return
    }
}

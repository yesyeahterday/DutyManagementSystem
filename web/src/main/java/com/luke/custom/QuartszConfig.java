package com.luke.custom;

import com.luke.config.NotificationConfig;
import com.luke.scheduler.EmailScheduleJob;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartszConfig {
    @Autowired
    private NotificationConfig notificationConfig;
    @Bean
    public JobDetail emailScheduleJobDetail() {
        return JobBuilder.newJob(EmailScheduleJob.class)
                .withIdentity("emailToNotificateTomorrowSchedue")
                .usingJobData("email", "user@example.com")
                .usingJobData("subject", "定时邮件")
                .usingJobData("content", "提醒值班")
                .storeDurably()
                .build();
    }
    @Bean
    public Trigger emailScheduleJobTrigger() {
        //每天下午18点检查
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(notificationConfig.getCronschedule());

        return TriggerBuilder.newTrigger()
                .forJob(emailScheduleJobDetail())
                .withIdentity("emailScheduleTrigger")
                .withSchedule(cronScheduleBuilder)
                .build();
    }
}

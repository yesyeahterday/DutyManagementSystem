package com.luke.controller.notification;

import com.luke.result.Result;
import com.luke.vo.NotificationVo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/notification")
public class NotificationController {
    // 1. 定时发送邮件给
    @GetMapping
    public Result<List<NotificationVo>> getUnreadNotifications() {
        // 返回当前用户的未读通知
        return Result.ok();
    }

    // 2. 标记已读
    @PutMapping("/{noticeId}/read")
    public Result markAsRead(Long noticeId) {
        // 更新消息状态
        return Result.ok();
    }
}


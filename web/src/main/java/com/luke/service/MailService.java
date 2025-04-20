package com.luke.service;

public interface MailService {
    void sendSimpleMail(String to, String subject, String content);
}

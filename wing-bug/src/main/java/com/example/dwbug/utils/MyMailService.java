package com.example.dwbug.utils;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 发送qq邮件
 */
@Component
public class MyMailService {
    @Resource
    JavaMailSender javaMailSender;

    @Value("${qqEmail.from}")
    private String from;

    public  void sendMail(String to,String subject,String text){
        System.out.println(from);
        SimpleMailMessage smm = new SimpleMailMessage();
        smm.setFrom(this.from);//发送者
        smm.setTo(to);//收件人
        smm.setCc(this.from);//抄送人
        smm.setSubject(subject);//邮件主题
        smm.setText(text);//邮件内容
        javaMailSender.send(smm);//发送邮件

    }
}


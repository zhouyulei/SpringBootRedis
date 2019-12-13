package com.boot.debug.redis.server.service;

import com.boot.debug.redis.model.entity.Notice;
import com.boot.debug.redis.model.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * 邮件服务
 * @Author:debug (SteadyJack)
 * @Link: weixin-> debug0868 qq-> 1948831260
 * @Date: 2019/10/30 14:58
 **/
@Service
public class EmailService {

    private static final Logger log= LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private Environment env;

    //TODO:给指定的用户发送通告
    public void emailUserNotice(Notice notice,User user){
        log.info("----给指定的用户：{} 发送通告：{}",user,notice);

        this.sendSimpleEmail(notice.getTitle(),notice.getContent(),user.getEmail());
    }

    //TODO:发送简单的邮件消息
    public void sendSimpleEmail(final String subject,final String content,final String ... tos){
        try {
            SimpleMailMessage message=new SimpleMailMessage();
            message.setSubject(subject);
            message.setText(content);
            message.setTo(tos);
            message.setFrom(env.getProperty("mail.send.from"));
            mailSender.send(message);

            log.info("----发送简单的邮箱完毕--->");
        }catch (Exception e){
            log.error("--发送简单的邮件消息,发生异常：",e.fillInStackTrace());
        }
    }
}




























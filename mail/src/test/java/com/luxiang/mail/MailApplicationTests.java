package com.luxiang.mail;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.test.context.junit4.SpringRunner;

import javax.mail.internet.MimeMessage;
import java.io.File;

@RunWith(SpringRunner.class)
@SpringBootTest
class MailApplicationTests {

    @Autowired
    JavaMailSenderImpl mailSender;

    @Test
    void contextLoads() {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setSubject("测试");
            helper.setText("大家，好！<br> &nbsp;&nbsp;<b style='color:red'>测试一下！</b> <br>谢谢！", true);
            helper.setTo("lux@haoshudao.com");
            helper.setFrom("1374920889@qq.com");

            //添加附件
            helper.addAttachment("云平台.jpg", new File("C:\\Users\\luxia\\Pictures\\云平台.jpg"));

            mailSender.send(mimeMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

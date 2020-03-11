package com.luxiang.rabbitmq.controller;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalTime;

@RestController
public class TestController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping("/test")
    public void test() {
        rabbitTemplate.convertAndSend("test_queue", "test_queue" + LocalTime.now());
        System.out.println(String.format("%s发送消息:%s", LocalTime.now(), "test_queue"));
    }
}

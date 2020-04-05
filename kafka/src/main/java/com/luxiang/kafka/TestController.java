package com.luxiang.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @Autowired
    private KafkaSender kafkaSender;

    @RequestMapping("/test")
    public void test() {
        kafkaSender.send();
    }
}

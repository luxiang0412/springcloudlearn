package com.luxiang.okhttpdemo;

import com.luxiang.okhttpdemo.utils.OkHttpUtil;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class OkhttpDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(OkhttpDemoApplication.class, args);
    }

    @GetMapping
    public void test() {
        for (int i = 0; i < 100; i++) {
            String s = OkHttpUtil.get("https://www.baidu.com", null);
            System.out.println(s);
        }
    }
}

package com.luxiang.redis;

import com.luxiang.redis.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;

@RestController
public class ApiController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RedisTemplate<String, String> strRedisTemplate;

    @Autowired
    private RedisTemplate<String, Serializable> serializableRedisTemplate;


    @RequestMapping(value = "/insert")
    public void insert(@RequestParam(value = "name", required = false) String name) {
        User user = new User();
        user.setId(1L);
        user.setName("luxiang");
        serializableRedisTemplate.opsForValue().set("luxiang", user);
    }

    @RequestMapping("/list")
    public Object list() {
        User luxiang = (User) serializableRedisTemplate.opsForValue().get("luxiang");
        return luxiang;
    }

    @RequestMapping("/delete/{id}")
    public void delete(@PathVariable Long id) {
        serializableRedisTemplate.delete("luxiang");
    }
}

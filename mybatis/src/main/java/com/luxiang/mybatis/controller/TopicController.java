package com.luxiang.mybatis.controller;

import com.luxiang.mybatis.mapper.TopicMapper;
import com.luxiang.mybatis.service.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TopicController {

    @Autowired
    private TopicService topicService;

    @Autowired
    private TopicMapper topicMapper;

    @GetMapping("/")
    public Object list() {
        return topicMapper.selectTitleList();
    }
}

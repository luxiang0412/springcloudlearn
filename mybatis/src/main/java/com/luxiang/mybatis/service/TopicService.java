package com.luxiang.mybatis.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.luxiang.mybatis.entity.Topic;
import com.luxiang.mybatis.mapper.TopicMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TopicService {

    @Autowired
    private TopicMapper topicMapper;

    public List<Topic> list() {
        return topicMapper.selectList(new QueryWrapper<>());
    }

}
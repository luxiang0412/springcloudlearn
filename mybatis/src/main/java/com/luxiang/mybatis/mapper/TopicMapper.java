package com.luxiang.mybatis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.luxiang.mybatis.entity.Topic;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface TopicMapper extends BaseMapper<Topic> {

    Topic selectByTitle(@Param("title") String title);

    List<Map> selectTitleList();
}

package com.luxiang.rocketmq;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;

import static com.fasterxml.jackson.databind.SerializationFeature.FAIL_ON_EMPTY_BEANS;

/**
 * @author luxiang
 * description  //jackson 单例
 * create       2021-05-11 11:47
 */
public class JacksonSingleton {

    private volatile static ObjectMapper OBJECT_MAPPER;

    private JacksonSingleton() {
    }

    public static ObjectMapper getSingleton() {
        if (OBJECT_MAPPER == null) {
            synchronized (JacksonSingleton.class) {
                if (OBJECT_MAPPER == null) {
                    OBJECT_MAPPER = new ObjectMapper();
                    OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
                    OBJECT_MAPPER.configure(FAIL_ON_EMPTY_BEANS, false);
                    OBJECT_MAPPER.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
                }
            }
        }
        return OBJECT_MAPPER;
    }
}

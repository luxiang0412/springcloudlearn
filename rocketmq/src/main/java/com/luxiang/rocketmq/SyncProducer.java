package com.luxiang.rocketmq;

import com.luxiang.rocketmq.model.Data;
import com.luxiang.rocketmq.model.Role;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * @author luxiang
 * description  //发送同步消息
 * create       2021-05-11 11:18
 */
public class SyncProducer {
    public static void main(String[] args) throws Exception {

        List<Role> roleList = new ArrayList<>();
        roleList.add(new Role(1L, "ROLE_ANALYST", "http://192.168.33.4:81/test.png", 0,
                null, null, "2021-05-08 17:51:23"));
        roleList.add(new Role(2L, "RO1LE_ANALYST", "http://192.168.33.4:81/test.png", 0,
                null, null, "2021-05-08 17:51:23"));
        roleList.add(new Role(3L, "ROLE_ANA11LYST", "http://192.168.33.4:81/test2.png", 0,
                null, null, "2021-05-08 17:51:23"));
        roleList.add(new Role(4L, "ROLE_ANALYST", "http://192.168.33.4:81/test2.png", 0,
                null, null, "2021-05-08 17:51:23"));
        roleList.add(new Role(5L, "111", "http://192.168.33.4:81/test2.png", 0,
                null, null, "2021-05-08 17:51:23"));
        roleList.add(new Role(6L, "111", "http://192.168.33.4:81/test2.png", 0,
                null, null, "2021-05-08 17:51:23"));
        roleList.add(new Role(7L, "111", "http://192.168.33.4:81/test2.png", 0,
                null, null, "2021-05-08 17:51:23"));
        roleList.add(new Role(8L, "111", "http://192.168.33.4:81/test2.png", 0,
                null, null, "2021-05-08 17:51:23"));
        roleList.add(new Role(9L, "111", "http://192.168.33.4:81/test2.png", 0,
                null, null, "2021-05-08 17:51:23"));
        roleList.add(new Role(10L, "111", "http://192.168.33.4:81/test2.png", 0,
                null, null, "2021-05-08 17:51:23"));

        Data data = new Data("yjglzh_t_role", roleList);

        // 实例化消息生产者Producer
        DefaultMQProducer producer = new DefaultMQProducer("CHEMCIAL_FLINK_SOURCE_GROUP");
        // 设置NameServer的地址
        producer.setNamesrvAddr("192.168.33.4:9876");
        // 启动Producer实例
        producer.start();
        // 创建消息，并指定Topic，Tag和消息体
        Message msg = new Message("chemical_flink_source100" /* Topic */,
                "insert" /* Tag */,
                (JacksonSingleton.getSingleton().writeValueAsString(data))
                        .getBytes(RemotingHelper.DEFAULT_CHARSET) /* Message body */
        );
        // 发送消息到一个Broker
        SendResult sendResult = producer.send(msg);
        // 通过sendResult返回消息是否成功送达
        System.out.printf("%s%n", sendResult);
        // 如果不再发送消息，关闭Producer实例。
        producer.shutdown();
    }
}

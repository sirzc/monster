package com.yunli.mq;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

/**
 * 生产者
 *
 * @author zhouchao
 * @date 2019-02-20 11:36
 */
public class Producer {

    public static void main(String[] args) throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer("ONLY_ONE");
        producer.setNamesrvAddr("127.0.0.1:9999");
        producer.start();
        for (int i = 0; i < 10; i++) {
            Message msg = new Message("TOPIC8","TAG1",("TOPIC8-TAG1-消息发送测试" + i).getBytes());
            SendResult result = producer.send(msg);
            System.err.println(result);
        }

        for (int i = 10; i < 20; i++) {
            Message msg = new Message("TOPIC8", "TAG2",("TOPIC8-TAG2-消息发送测试" + i).getBytes());
            SendResult result = producer.send(msg);
            System.err.println(result);
        }

        producer.shutdown();
    }
}

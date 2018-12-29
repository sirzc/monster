package com.yunli.monster.core.mq;

import com.yunli.monster.DateUtil;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author zhouchao
 * @create 2018-12-19 16:41
 */
public class Producer {

    private static List<String> orderIds =
            Arrays.asList("201812190001", "201812190002", "201812190003", "201812190004", "201812190005", "201812190006");

    public static void test1() throws MQClientException, InterruptedException {

        DefaultMQProducer producer = new DefaultMQProducer("OrderProducer");

        producer.setNamesrvAddr("10.1.55.180:9876");
        //     producer.setVipChannelEnabled(false);//3.2.6的版本没有该设置，在更新或者最新的版本中务必将其设置为false，否则会有问题
        // 发送失败重试次数
        producer.setRetryTimesWhenSendFailed(3);

        //调用start()方法启动一个producer实例
        producer.start();

        //发送10条消息到Topic为TopicTest，tag为TagA，消息内容为“Hello RocketMQ”拼接上i的值
        for (String oid : orderIds) {
            try {
                Message msg = new Message("OrderTopic", "20181219", (oid).getBytes("utf-8"));
                //调用producer的send()方法发送消息
                //这里调用的是同步的方式，所以会有返回结果
                producer.send(msg);
                System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "," + oid);
            } catch (Exception e) {
                e.printStackTrace();
                Thread.sleep(1000);
            }
        }

        //发送完消息之后，调用shutdown()方法关闭producer
        producer.shutdown();
    }

    public static void test2() throws MQClientException, InterruptedException {
        DefaultMQProducer producer = new DefaultMQProducer("OrderProducer");
        producer.setNamesrvAddr("192.168.199.159:9876");
        //调用start()方法启动一个producer实例
        producer.start();
        //发送10条消息到Topic为TopicTest，tag为TagA，消息内容为“Hello RocketMQ”拼接上i的值
        for (int i = 0; i < orderIds.size(); i++) {
            try {
                Message msg = new Message("OrderTopic1", DateUtil.getDay(), DateUtil.getDay(),
                        (DateUtil.getTime() + ":" + orderIds.get(i)).getBytes("utf-8"));
                SendResult result = producer.send(msg, (mqs, msg1, arg) -> {
                    Integer id = (Integer) arg;
                    int index = id % mqs.size();
                    return mqs.get(index);
                }, 106);
                System.err.println(result);
            } catch (Exception e) {
                e.printStackTrace();
                Thread.sleep(1000);
            }
        }
        //发送完消息之后，调用shutdown()方法关闭producer
//        producer.shutdown();
    }

    public static void main(String[] args) throws MQClientException, InterruptedException {
        Producer.test2();
    }
}

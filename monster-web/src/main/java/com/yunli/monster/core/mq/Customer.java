package com.yunli.monster.core.mq;

import com.yunli.monster.core.util.DateUtil;
import org.apache.rocketmq.client.consumer.DefaultMQPullConsumer;
import org.apache.rocketmq.client.consumer.PullResult;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * 消费者
 *
 * @author zhouchao
 * @create 2018-12-29 18:41
 */
public class Customer {

    private static DefaultMQPullConsumer consumer = null;

    static {
        consumer = new DefaultMQPullConsumer("ZC_GROUP_4");
        consumer.setNamesrvAddr("10.1.55.180:9876");
        try {
            consumer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }
    }

    public static  MessageQueue getMessageQueue(List<MessageQueue> mgs, Object arg) {
        Integer code = (Integer) arg;
        int index = code % mgs.size();
        return mgs.get(index);
    }

    public static synchronized void test(int maxNums) throws Exception {
        // 拉取所有消息
        Set<MessageQueue> mqs = consumer.fetchSubscribeMessageQueues("OrderTopic1");
        consumer.getOffsetStore().load();
        List<MessageQueue> linkedList = new ArrayList<>(mqs);
        Collections.sort(linkedList);
        MessageQueue mq = getMessageQueue(linkedList, 106);
        // 对于从存储端获取消费进度
        long offset = consumer.fetchConsumeOffset(mq, true);
        PullResult pullResult = consumer.pullBlockIfNotFound(mq, DateUtil.getDay(), offset, maxNums);
        consumer.getOffsetStore().updateOffset(mq, pullResult.getNextBeginOffset(), false);
        consumer.getOffsetStore().persist(mq);
        switch (pullResult.getPullStatus()) {
            case FOUND:
                List<MessageExt> mes = pullResult.getMsgFoundList();
                for (MessageExt me : mes) {
                    System.err.println(new String(me.getBody(), "utf-8"));
                }
                break;
            case NO_MATCHED_MSG:
                break;
            case NO_NEW_MSG:
                System.err.println("没有新消息");
                break;
            case OFFSET_ILLEGAL:
                break;
            default:
                break;

        }
    }

    public static void main(String[] args) {
        new Thread(() -> {
            try {
                test(3);
                System.err.println("========================【3】=====================");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        new Thread(() -> {
            try {
                test(5);
                System.err.println("========================【5】=====================");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        new Thread(() -> {
            try {
                test(2);
                System.err.println("========================【2】=====================");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}



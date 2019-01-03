package com.yunli.monster.core.mq.core;

import org.apache.rocketmq.client.consumer.DefaultMQPullConsumer;
import org.apache.rocketmq.client.consumer.PullResult;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * 消费者
 *
 * @author zhouchao
 * @create 2019-01-02 14:31
 */
public class OrderCustomer {
    
    private static final Logger                logger   = LoggerFactory.getLogger(OrderCustomer.class);

    private static       DefaultMQPullConsumer consumer = new DefaultMQPullConsumer("ZC_GROUP_5");

    public void init(){
        consumer.setNamesrvAddr("10.1.55.180:9876");
        try {
            consumer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }
    }

    public static <T> T getMessageQueue(List<T> mqs, Object tags) {
        int code = tags.hashCode();
        int index = code % mqs.size();
        return mqs.get(Math.abs(index));
    }

    public static Boolean checkMessage(PullResult pullResult) {
        Boolean result = null;
        switch (pullResult.getPullStatus()) {
            case FOUND:
                result = true;
                break;
            case NO_MATCHED_MSG:
                result = false;
                break;
            case NO_NEW_MSG:
                break;
            case OFFSET_ILLEGAL:
                result = false;
                break;
            default:
                result = false;
                break;
        }
        return result;
    }

    public synchronized List<MessageExt> listMessage(String topic,String tags,int maxNum) {
        // 拉取所有消息
        try {
            Set<MessageQueue> mqs = consumer.fetchSubscribeMessageQueues(topic);
            consumer.getOffsetStore().load();
            List<MessageQueue> linkedList = new ArrayList<>(mqs);
            Collections.sort(linkedList);
            MessageQueue mq = getMessageQueue(linkedList, tags);
            // 对于从存储端获取消费进度
            long offset = consumer.fetchConsumeOffset(mq, true);
            PullResult pullResult = consumer.pullBlockIfNotFound(mq, "*", offset, maxNum);
            consumer.getOffsetStore().updateOffset(mq, pullResult.getNextBeginOffset(), false);
            consumer.getOffsetStore().persist(mq);
            Boolean check = checkMessage(pullResult);
            if (true == check ) {
                return pullResult.getMsgFoundList();
            } else  {
                return null;
            }
        } catch (Exception e){
            logger.error("单号获取异常");
            e.printStackTrace();
            return null;
        }
    }

}

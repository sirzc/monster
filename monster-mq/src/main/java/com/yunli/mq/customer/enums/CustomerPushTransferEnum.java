package com.yunli.mq.customer.enums;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Lists;
import com.yunli.mq.common.MessageData;
import com.yunli.mq.customer.handler.IMqHandler;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * push方式下，消费方式
 *
 * @author zhouchao
 * @date 2019-01-18 15:09
 */
public enum CustomerPushTransferEnum {

    /**
     * 同时push
     */
    PUSH_DISORDER {
        @Override
        public void registerHandlers(DefaultMQPushConsumer consumer, Map<String, List<IMqHandler>> topicHandlers) {
            final Logger logger = LoggerFactory.getLogger(CustomerPushTransferEnum.class);
            MessageListenerConcurrently listener = (msgs, context) -> {
                boolean ok = StandarddealMessage(topicHandlers, msgs, logger);
                if (ok) {
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                } else {
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }
            };
            consumer.setMessageListener(listener);
        }

    },
    /**
     * 顺序push
     */
    PUSH_ORDERLY {
        @Override
        public void registerHandlers(DefaultMQPushConsumer consumer, Map<String, List<IMqHandler>> topicHandlers) {
            final Logger logger = LoggerFactory.getLogger(CustomerPushTransferEnum.class);
            MessageListenerOrderly listener = (msgs, context) -> {
                boolean ok = StandarddealMessage(topicHandlers, msgs, logger);
                if (ok) {
                    return ConsumeOrderlyStatus.SUCCESS;
                } else {
                    return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
                }
            };
            consumer.setMessageListener(listener);
        }

    };

    CustomerPushTransferEnum() {

    }

    public abstract void registerHandlers(DefaultMQPushConsumer consumer, Map<String, List<IMqHandler>> topicHandlers);

    private static boolean StandarddealMessage(Map<String, List<IMqHandler>> topicHandlers, List<MessageExt> msgs,
                                               final Logger logger) {
        boolean ok = true;
        // 该map的key为topic，value为该topic下的所有message
        Map<String, List<MessageExt>> topicMessage = msgs.stream().collect(Collectors.groupingBy(MessageExt::getTopic));

        for (Map.Entry<String, List<MessageExt>> entry : topicMessage.entrySet()) {
            String topic = entry.getKey();
            List<MessageData> messages = Lists.newArrayList();
            for (MessageExt messageExt : entry.getValue()) {
                String message = new String(messageExt.getBody(), StandardCharsets.UTF_8);
                MessageData messageData = JSON.parseObject(message, new TypeReference<MessageData>() {});
                messages.add(messageData);
            }
            List<IMqHandler> handlers = topicHandlers.get(topic);
            for (IMqHandler handler : handlers) {
                try {
                    handler.handle(messages);
                } catch (Exception e) {
                    logger.error("unitop mq error,topic:{},handler:{},messageSize:{},messages:{}", topic,
                            handler.getClass().getName(), messages.size(), messages, e);
                    ok = false;
                }
            }
        }
        return ok;
    }

}

package com.yunli.mq.customer.push;

import com.yunli.mq.customer.config.ConsumerBuildConfig;
import com.yunli.mq.exception.MqConsumerConfigException;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * unitop-com.yunli.mq.mq push消费者
 *
 * @author zhouchao
 * @date 2019-01-18 15:09
 */
public class UnitopMQPushConsumer {
    private static final Logger logger = LoggerFactory.getLogger(UnitopMQPushConsumer.class);

    private ConsumerBuildConfig config;

    private DefaultMQPushConsumer consumer;

    public UnitopMQPushConsumer(ConsumerBuildConfig config) {
        this.config = config;
        init();
    }

    /**
     * 初创建push消费者
     */
    private void init() {
        if (null == config) {
            throw new MqConsumerConfigException("com.yunli.mq.mq init error,build config is null");
        }
        try {
            // 创建消费者实体
            consumer = new DefaultMQPushConsumer();
            consumer.setConsumerGroup(config.getGroupName());
            consumer.setNamesrvAddr(config.getNameServer());
            consumer.subscribe(config.getTopic(), config.getSubExpression());
            if (!StringUtils.isEmpty(config.getInstanceName())) {
                consumer.setInstanceName(config.getInstanceName());
            }
            consumer.setConsumeMessageBatchMaxSize(config.getConsumeMessageBatchMaxSize());
            consumer.setConsumeThreadMin(config.getThreadCountMin());
            consumer.setConsumeThreadMax(config.getThreadCountMax());
            logger.info("com.yunli.mq.mq init {},Rocket MQ:DefaultMQPushConsumer use config:{}", config.getGroupName(), config);
        } catch (MQClientException e) {
            String warn = "com.yunli.mq.mq init" + config.getGroupName() + " error, use config:" + config;
            logger.error(warn, e);
            throw new MqConsumerConfigException(warn, e);
        }
    }

    /**
     * 启动消费者
     */
    public void run() throws MQClientException {
        if (null == config.getTopicHandler() || config.getTopicHandler().size() == 0) {
            throw new MqConsumerConfigException("com.yunli.mq.mq consumer config topic listener is null");
        }
        if (config.isBroadcast()) {
            consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
            consumer.setMessageModel(MessageModel.BROADCASTING);
        }
        // 是否为顺序模式，注册处理程序
        if (config.isOrderly()) {
            CustomerPushTransferEnum.PUSH_ORDERLY.registerHandlers(consumer, config.getTopicHandler());
        } else {
            CustomerPushTransferEnum.PUSH_DISORDER.registerHandlers(consumer, config.getTopicHandler());
        }
        start();
        logger.info("com.yunli.mq.mq start push consumer use config:{}", config.getGroupName());
    }

    /**
     * 启动消费者
     *
     * @throws MQClientException
     */
    public void start() throws MQClientException {
        consumer.start();
    }

    /**
     * 关闭消费者
     */
    public void shutdown() {
        consumer.shutdown();
    }
}

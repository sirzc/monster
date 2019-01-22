package com.yunli.mq.customer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 消费者工厂类
 *
 * @author zhouchao
 * @date 2019-01-18 16:24
 */
public class MqConsumerFactory {

    private static final Logger logger = LoggerFactory.getLogger(MqConsumerFactory.class);

    private static MqConsumerFactory factory;

    private MqConsumerFactory() {
        super();
    }

    /**
     * 获取消费者工厂类
     * @return
     */
    public static MqConsumerFactory getFactory() {
        if (factory == null) {
            synchronized (MqConsumerFactory.class) {
                if (factory == null) {
                    factory = new MqConsumerFactory();
                }
            }
        }
        return factory;
    }

    /**
     * 获取消费者实例
     *
     * @param prefix 引用配置文件前缀（切换配置）
     * @return
     */
    public FastMqPushConsumer getPushConsumer(String prefix) {
        FastMqPushConsumer unitopMQPushConsumer;
        try {
            unitopMQPushConsumer = new FastMqPushConsumer(prefix);
            logger.info("mq new consumer load local config:{}", prefix);
        } catch (Exception e) {
            logger.error("mq new consumer error. load local config: {} error.Cause:", prefix, e);
            throw e;
        }
        return unitopMQPushConsumer;
    }
}

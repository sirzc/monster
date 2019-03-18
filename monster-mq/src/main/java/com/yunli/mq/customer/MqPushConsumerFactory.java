package com.yunli.mq.customer;

import com.yunli.mq.customer.config.ConsumerBuildConfig;
import com.yunli.mq.customer.push.UnitopMQPushConsumer;
import com.yunli.mq.customer.util.ConfigLoadUtil;
import com.yunli.mq.exception.MqConsumerConfigException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 消费者工厂类
 *
 * @author zhouchao
 * @date 2019-01-18 16:24
 */
public class MqPushConsumerFactory {

    /**
     * push类型 消费者实例对象集合
     */
    private static Map<String, UnitopMQPushConsumer> consumers = new ConcurrentHashMap<>();

    /**
     * 获取push消费者实例
     *
     * @param prefix 配置属性的前缀
     * @return
     */
    public static UnitopMQPushConsumer getPushConsumer(String prefix) {
        if (consumers.size() == 0 || !consumers.containsKey(prefix)) {
            synchronized (MqPullConsumerFactory.class) {
                if (consumers.size() == 0 || !consumers.containsKey(prefix)) {
                    initPushConsumer(prefix);
                }
            }
        }
        return consumers.get(prefix);
    }

    /**
     * 构建 push 消费者
     *
     * @param prefix
     */
    private static void initPushConsumer(String prefix) {
        if (consumers.containsKey(prefix)) {
            return;
        }
        if (!ConfigLoadUtil.checkValid(prefix)) {
            throw new MqConsumerConfigException(
                    "com.yunli.mq.mq build push consumer error,properties nameServer or topic or listener is null.");
        }
        ConsumerBuildConfig config = ConfigLoadUtil.getBuildConfig(prefix);
        config.setTopicHandler(ConfigLoadUtil.registerTopicHandler(config.getTopic(), prefix));
        consumers.put(prefix, new UnitopMQPushConsumer(config));
    }

}

package com.yunli.mq.customer;

import com.yunli.mq.customer.pull.FastMqPullConsumer;
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
public class MqPullConsumerFactory {

    /**
     * pull类型 消费者实例对象集合
     */
    private static Map<String, FastMqPullConsumer> consumers = new ConcurrentHashMap<String, FastMqPullConsumer>();

    /**
     * 获取pull消费者实例
     *
     * @param prefix 配置属性的前缀
     * @return
     */
    public static FastMqPullConsumer getPullConsumer(String prefix) {
        if (consumers.size() == 0 || !consumers.containsKey(prefix)) {
            synchronized (MqPullConsumerFactory.class) {
                if (consumers.size() == 0 || !consumers.containsKey(prefix)) {
                    initPullConsumer(prefix);
                }
            }
        }
        return consumers.get(prefix);
    }

    /**
     * 构建pull消费者
     *
     * @param prefix
     */
    private static void initPullConsumer(String prefix) {
        if (consumers.containsKey(prefix)) {
            return;
        }
        if (!ConfigLoadUtil.checkCommon(prefix)) {
            throw new MqConsumerConfigException("com.yunli.mq.mq build pull consumer error,properties nameServer or topic is null.");
        }
        consumers.put(prefix, new FastMqPullConsumer(ConfigLoadUtil.getBuildConfig(prefix)));
    }
}

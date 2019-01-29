package com.yunli.mq.customer;

import com.yunli.mq.exception.MqConsumerConfigException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 消费者管理类
 *
 * @author zhouchao
 * @date 2019-01-18 16:19
 */
public class MqConsumerManager {

    private static final Logger logger = LoggerFactory.getLogger(MqConsumerManager.class);

    /**
     * 初始化默认配置
     */
    public static void init() {
        MqConsumerFactory factory = MqConsumerFactory.getFactory();
        List<String> prefixs = factory.listPrefix();
        for (String prefix : prefixs) {
            factory.buildConsumer(prefix);
            try {
                factory.getConsumer(prefix).run();
            } catch (MQClientException e) {
                String warn = "mq start consumer error. use config: " + prefix;
                logger.error(warn, e);
                throw new MqConsumerConfigException(warn, e);
            }
        }
    }

}

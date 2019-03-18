package com.yunli.mq.customer;

import com.yunli.mq.customer.pull.FastMqPullConsumer;
import com.yunli.mq.customer.util.ConfigLoadUtil;
import com.yunli.mq.exception.MqConsumerConfigException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
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
     *
     * @param prefix
     */
    public static void init(String... prefix) {
        List<String> prefixs = new ArrayList<>();
        if (prefix == null || prefix.length == 0) {
            prefixs.addAll(ConfigLoadUtil.listPrefix());
        } else {
            prefixs.addAll(Arrays.asList(prefix));
        }
        initPush(prefixs);
    }

    /**
     * 启动push类型消费者
     *
     * @param prefixs
     */
    public static void initPush(List<String> prefixs) {
        for (String prefix : prefixs) {
            try {
                MqPushConsumerFactory.getPushConsumer(prefix).run();
            } catch (MQClientException e) {
                String warn = "com.yunli.mq.mq start consumer error. use config: " + prefix;
                logger.error(warn, e);
                throw new MqConsumerConfigException(warn, e);
            }
        }
    }

    /**
     * 获取pull类型消费者
     *
     * @param prefix
     * @return
     */
    public static FastMqPullConsumer getPullConsumer(String prefix) {
        return MqPullConsumerFactory.getPullConsumer(prefix);
    }

}

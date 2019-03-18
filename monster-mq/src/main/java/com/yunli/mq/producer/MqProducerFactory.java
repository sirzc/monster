package com.yunli.mq.producer;

import com.yunli.mq.common.MqConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 生产者 工厂类
 *
 * @author zhouchao
 * @date 2019-01-16 21:17
 */
public class MqProducerFactory {
    private static final Logger logger = LoggerFactory.getLogger(MqProducerFactory.class);

    /**
     * 生产者实例对象集合
     */
    private static Map<String, FastMqProducer> producers = new ConcurrentHashMap<String, FastMqProducer>();

    /**
     * 获取默认生产者
     *
     * @return
     */
    public static FastMqProducer getProducer() {
        // 设置默认前缀
        return getProducer(MqConstant.DEFAULT_MQ_PREFIX);
    }

    /**
     * 获取指定生产者
     *
     * @param prefix 配置属性的前缀
     * @return
     */
    public static FastMqProducer getProducer(String prefix) {
        try {
            if (producers.size() == 0 || !producers.containsKey(prefix)) {
                synchronized (MqProducerFactory.class) {
                    if (producers.size() == 0 || !producers.containsKey(prefix)) {
                        init(prefix);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("producer init:{} error.error message {}", prefix, e);
        }
        return producers.get(prefix);
    }

    /**
     * 初始化不同的生产者
     *
     * @param prefix 配置属性的前缀
     */
    private static void init(String prefix) {
        logger.info(">>>>>>>>>>>>> com.yunli.mq.mq producer load:{} properties", prefix);
        producers.put(prefix, new FastMqProducer(prefix));
    }

}

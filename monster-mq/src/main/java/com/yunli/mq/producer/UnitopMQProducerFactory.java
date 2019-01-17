package com.yunli.mq.producer;

import com.yunli.mq.common.MQConstantPool;
import com.yunli.mq.exception.MqProducerConfigException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 生产者 工厂类
 *
 * @author zhouchao
 * @date 2019-01-16 21:17
 */
public class UnitopMQProducerFactory {
    private static final Logger logger = LoggerFactory.getLogger(UnitopMQProducerFactory.class);

    private static UnitopMQProducer producer;

    /**
     * 获取默认配置生产者
     *
     * @return
     */
    public static UnitopMQProducer getProducer() {
        try {
            if (producer == null) {
                synchronized (UnitopMQProducerFactory.class) {
                    if (producer == null) {
                        init();
                    }
                }
            }
        } catch (Exception e) {
            logger.error("producer init error.error message {}", e);
        }
        return producer;
    }


    /**
     * 生产者初始化
     */
    private static void init() {
        Properties prop = getConfigProperties();
        buildProducer(prop);
    }

    /**
     * 获取生产者静态配置
     *
     * @return
     */
    private static Properties getConfigProperties() {
        // 从配置文件读取
        Properties props = new Properties();
        // 获取配置文件路径
        String filePath = null;
        if (MQConstantPool.ROOT_DIR.trim().length() > 0) {
            filePath = MQConstantPool.ROOT_DIR + "/" + MQConstantPool.DEFAULT_PRODUCER_FILE;
        } else {
            filePath = MQConstantPool.DEFAULT_PRODUCER_FILE;
        }
        try {
            InputStream in = UnitopMQProducerFactory.class.getClassLoader().getResourceAsStream(filePath);
            props.load(in);
        } catch (IOException e) {
            String warn = "unitop-mq wrong.producer read config io error. configPath:" + filePath;
            logger.warn(warn, e);
            throw new MqProducerConfigException(warn, e);
        }
        return props;
    }

    /**
     * 创建消费者
     *
     * @param prop
     */
    private static void buildProducer(Properties prop) {
        logger.info("unitop-mq running. producer use:{}", prop);
        producer = new UnitopMQProducer(prop);
    }

}

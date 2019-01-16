package com.yunli.mq.producer;

import com.lede.tech.rocketmq.easyclient.common.constant.MQConstant;
import com.lede.tech.rocketmq.easyclient.common.exception.MqConsumerConfigException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @Desc
 * @Author bjguosong
 * @Author ykhu
 */
public class UnitopMQProducerFactory {
    private static final Logger         logger = LoggerFactory.getLogger(UnitopMQProducerFactory.class);

    private static UnitopMQProducer producer;

    public static UnitopMQProducer getProducer(String producerGroup) {
        try {
            if (producer == null) {
                synchronized (UnitopMQProducerFactory.class) {
                    if (producer == null) {
                        init(producerGroup);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("easymq wrong. producer init error.", e);
        }
        return producer;
    }

    private static void init(String producerGroup) {
        Properties prop = getConfigProp();
        buildProducer(prop);
    }

    private static Properties getConfigProp() {
        //从配置文件读取
        Properties props = new Properties();
        String filePath = MQConstant.CONFIG_DIR + "/" + MQConstant.CONFIG_DIR_PRODUCERS + "/"
                + MQConstant.DEFAULT_PRODUCER_FILENAME;
        try {
            InputStream in = EasyMQProducerFactory.class.getClassLoader().getResourceAsStream(filePath);
            props.load(in);
            //			props.load(Files.newInputStream(configPath));
        } catch (IOException e) {
            String warn = "easymq wrong. producer read config io error. configPath:" + filePath;
            LOG.fatal(warn, e);
            throw new MqConsumerConfigException(warn, e);
        }
        return props;
    }

    private static void buildProducer(Properties prop) {
        LOG.info("easymq running. producer use:" + prop);
        producer = new EasyMQProducer(prop);
    }

}

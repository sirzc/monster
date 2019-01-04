package com.yunli.monster.core.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * mq相关配置
 *
 * @author zhouchao
 * @create 2019-01-04 14:55
 */
@Configuration
@ConfigurationProperties(prefix = MqProperties.PREFIX)
public class MqProperties{

    public static final String PREFIX = "mq";

    /**
     * 服务地址
     */
    private String nameServer;

    /**
     * 消费者名称
     */
    private String consumerGroup;

    /**
     * 生成者名称
     */
    private String producerGroup;

    public String getNameServer() {
        return nameServer;
    }

    public void setNameServer(String nameServer) {
        this.nameServer = nameServer;
    }

    public String getConsumerGroup() {
        return consumerGroup;
    }

    public void setConsumerGroup(String consumerGroup) {
        this.consumerGroup = consumerGroup;
    }

    public String getProducerGroup() {
        return producerGroup;
    }

    public void setProducerGroup(String producerGroup) {
        this.producerGroup = producerGroup;
    }

    @Override
    public String toString() {
        return "MqProperties{" + "nameServer='" + nameServer + '\'' + ", consumerGroup='" + consumerGroup + '\''
                + ", producerGroup='" + producerGroup + '\'' + '}';
    }
}

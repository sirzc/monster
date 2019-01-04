package com.yunli.monster.core.config;

import com.yunli.monster.core.mq.core.OrderCustomer;
import com.yunli.monster.core.mq.core.OrderProducer;
import com.yunli.monster.core.properties.MqProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhouchao
 * @create 2019-01-02 13:52
 */
@Configuration
public class MqConfig {

    @Autowired
    MqProperties mqProperties;

    @Bean(initMethod = "init", destroyMethod = "close")
    public OrderProducer orderProducer() {
        OrderProducer producer = new OrderProducer();
        producer.setNameServer(mqProperties.getNameServer());
        producer.setProducerGroup(mqProperties.getProducerGroup());
        return producer;
    }

    @Bean(initMethod = "init")
    public OrderCustomer orderCustomer() {
        OrderCustomer customer = new OrderCustomer();
        customer.setNameServer(mqProperties.getNameServer());
        customer.setCustomerGroup(mqProperties.getProducerGroup());
        return customer;
    }
}

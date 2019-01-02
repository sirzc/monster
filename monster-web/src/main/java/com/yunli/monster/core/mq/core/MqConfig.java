package com.yunli.monster.core.mq.core;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhouchao
 * @create 2019-01-02 13:52
 */
@Configuration
public class MqConfig {

    @Bean(initMethod = "init", destroyMethod = "close")
    public OrderProducer orderProducer() {
        return new OrderProducer();
    }

    @Bean(initMethod = "init")
    public OrderCustomer orderCustomer() {
        return new OrderCustomer();
    }
}
